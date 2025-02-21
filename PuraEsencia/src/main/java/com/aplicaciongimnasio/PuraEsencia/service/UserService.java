package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AssignRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.model.HealthRecord;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.security.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encryptedPassword);

        user.setIsActive(true);

        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean logicDelete(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userOptional.get().setIsActive(false);
            userRepository.save(userOptional.get());
            return true;
        }
        return false;
    }

    public User updateUser(String email, User updatedUser) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (updatedUser.getRoutine() != null) {
            user.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        return userRepository.save(user);
    }

    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public User assignRoutineToUser(AssignRoutineRequest assignRoutineRequest) {
        User user = userRepository.findById(assignRoutineRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Routine routine = routineRepository.findById(assignRoutineRequest.getRoutineId())
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        System.out.println(assignRoutineRequest);
        User trainer = userRepository.findById(assignRoutineRequest.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer no encontrado"));

        user.setRoutine(routine);
        user.setTrainer(trainer);
        return userRepository.save(user);
    }

    public Routine getUserRoutine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return user.getRoutine();
    }

    public void assignClientToTrainer(Long clientId, Long trainerId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        if (!trainer.getRole().equals(Role.TRAINER)) {
            throw new IllegalArgumentException("El usuario seleccionado no es un entrenador");
        }

        client.setTrainer(trainer);
        userRepository.save(client);
    }

    public List<User> getClientsByTrainerId(Long trainerId) {
        return userRepository.findByTrainerIdAndRole(trainerId, Role.CLIENT_GYM);
    }

    public List<User> getAllByRole(String role) {
        if (role.equalsIgnoreCase("CLIENTS")) {
            // Aquí se convierte a un List<Role> a partir de los strings de roles
            List<Role> roles = Stream.of("CLIENT_GYM", "CLIENT_CLASSES", "CLIENT_BOTH")
                    .map(Role::valueOf)  // Convierte el String en el enum correspondiente
                    .collect(Collectors.toList());

            // Llama al repositorio con la lista de roles
            return userRepository.findAllByRoleInAndIsActive(roles, true);
        } else {
            try {
                // Convierte el role a un enum si no es "CLIENTS"
                Role userRole = Role.valueOf(role.toUpperCase());
                return userRepository.findAllByRoleAndIsActive(userRole, true);
            } catch (IllegalArgumentException e) {
                // Manejo de error si el String no es un valor válido del enum
                throw new IllegalArgumentException("Role no válido: " + role);
            }
        }
    }

    public Optional<User> getTrainerByOneClient(Long clientId){
        Optional<User> user = userRepository.findById(clientId);
        return Optional.ofNullable(user.get().getTrainer());
    }

    public Boolean assignHealthRecord(Long userId, HealthRecord HealthRecord){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        user.setHealthRecord(HealthRecord);

        userRepository.save(user);

        return true;

    }
}

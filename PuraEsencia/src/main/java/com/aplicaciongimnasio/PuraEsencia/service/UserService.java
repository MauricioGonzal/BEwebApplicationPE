package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AssignRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.UserRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.GymRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineSetRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
    private RoutineSetRepository routineSetRepository;

    @Autowired
    private GymRepository gymRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User createUser(User user) {
        if(userRepository.findByEmailAndIsActive(user.getEmail(), true).isPresent()){
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encryptedPassword);

        user.setIsActive(true);

        return userRepository.save(user);
    }

    public User createUserByAdmin(UserRequest userRequest) {
        if(userRepository.findByEmailAndIsActive(userRequest.getEmail(), true).isPresent()){
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        User admin = userRepository.findById(userRequest.getAdminId()).orElseThrow(()-> new RuntimeException("ERROR. Contactar con soporte."));

        String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());

        User user = new User();
        user.setPassword(encryptedPassword);
        user.setEmail(userRequest.getEmail());
        user.setFullName(userRequest.getFullName());
        if(!Objects.equals(userRequest.getRole().toString(), "CLIENT")) user.setRole(Role.valueOf(userRequest.getRole()));
        user.setGym(admin.getGym());

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
        return userRepository.findByEmailAndIsActive(email, true);
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
        User user = userRepository.findByEmailAndIsActive(email, true)
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

        User trainer = userRepository.findById(assignRoutineRequest.getTrainerId())
                .orElseThrow(() -> new RuntimeException("Trainer no encontrado"));

        user.setRoutine(routine);
        user.setTrainer(trainer);
        return userRepository.save(user);
    }

    public List<RoutineSet> getUserRoutine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<RoutineSet> routineSets = routineSetRepository.findByRoutine(user.getRoutine());

        return routineSets;
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
            List<Role> roles = Stream.of("CLIENT_GYM", "CLIENT_CLASSES","CLIENT_BOTH")
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

            return userRepository.findAllByRoleInAndIsActiveOrRoleIsNull(roles, true);
        } else {
            try {
                Role userRole = Role.valueOf(role.toUpperCase());
                return userRepository.findAllByRoleAndIsActive(userRole, true);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Role no válido: " + role);
            }
        }
    }

    public List<User> getAllForAssistance() {
        List<Role> roles = Stream.of("CLIENT_GYM", "CLIENT_CLASSES","CLIENT_BOTH")
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

        return userRepository.findAllForAssistance(roles);
    }

    public List<User> getAllGymUsers() {
            List<Role> roles = Stream.of("CLIENT_GYM", "CLIENT_BOTH")
                    .map(Role::valueOf)  // Convierte el String en el enum correspondiente
                    .collect(Collectors.toList());

            return userRepository.findAllByRoleInAndIsActive(roles, true);

    }

    public List<User> getAll() {
        return userRepository.findByIsActive(true);

    }

    public List<User> getAllForSalary() {
            List<Role> roles = Stream.of("TRAINER", "RECEPTIONIST")
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

            return userRepository.findActiveUsersWithoutSalaries(roles);
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

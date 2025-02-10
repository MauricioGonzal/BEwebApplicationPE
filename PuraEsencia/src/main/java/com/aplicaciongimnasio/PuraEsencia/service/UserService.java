package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AssignRoutineRequest;
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

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Este método crea un nuevo usuario con la contraseña encriptada
    public User createUser(User user) {
        // Encriptar la contraseña antes de guardarla
        String encryptedPassword = passwordEncoder.encode(user.getPassword());

        // Asigna la contraseña encriptada al usuario
        user.setPassword(encryptedPassword);

        // Guarda el usuario en la base de datos (suponiendo que tienes un repositorio)
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            userRepository.deleteByEmail(email);  // Elimina el usuario por username
            return true;
        }
        return false;  // Si no se encuentra, retorna false
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(String email, User updatedUser) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Actualizar los campos del usuario
        if (updatedUser.getRoutine() != null) {
            user.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        if (updatedUser.getPassword() != null) {
            // Encriptar la nueva contraseña
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        return userRepository.save(user);
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

    // Asignar un cliente a un entrenador
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

    // Obtener clientes de un entrenador
    public List<User> getClientsByTrainerId(Long trainerId) {
        return userRepository.findByTrainerIdAndRole(trainerId, Role.CLIENT);
    }

    // Obtener todos los user por role
    public List<User> getAllByRole(Role role) {
        return userRepository.findAllByRole(role);
    }

    public Optional<User> getTrainerByOneClient(Long clientId){
        Optional<User> user = userRepository.findById(clientId);
        return Optional.ofNullable(user.get().getTrainer());
    }
}

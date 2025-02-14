package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSession;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.WorkoutSessionRepository;
import com.aplicaciongimnasio.PuraEsencia.service.WorkoutSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/workout-sessions")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequiredArgsConstructor
public class WorkoutSessionController {

    @Autowired
    private WorkoutSessionService workoutSessionService;

    private final WorkoutSessionRepository workoutSessionRepository;
    private final UserRepository userRepository;

    // 🔹 Crear una nueva sesión de entrenamiento para un cliente
    @PostMapping("/{clientId}")
    public ResponseEntity<WorkoutSession> createSession(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setDate(new Date()); // Se registra la fecha actual
        WorkoutSession savedSession = workoutSessionRepository.save(session);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedSession);
    }

    // 🔹 Obtener todas las sesiones de un cliente
    @GetMapping("/userId/{clientId}")
    public ResponseEntity<List<WorkoutSession>> getSessionsByClient(@PathVariable Long userId) {
        List<WorkoutSession> sessions = workoutSessionRepository.findByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    // 🔹 Obtener una sesión específica por ID
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<WorkoutSession> getSessionById(@PathVariable Long sessionId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));
        return ResponseEntity.ok(session);
    }

    // 🔹 Eliminar una sesión (y sus registros de ejercicios)
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long sessionId) {
        if (!workoutSessionRepository.existsById(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada");
        }
        workoutSessionRepository.deleteById(sessionId);
        return ResponseEntity.noContent().build();
    }



    @PostMapping
    public ResponseEntity<?> saveWorkoutSession(@RequestBody WorkoutSessionRequest request) {
        WorkoutSession session = workoutSessionService.saveWorkoutSession(request);
        return ResponseEntity.ok(session);
    }

    // Endpoint para obtener las sesiones de entrenamiento de un usuario
    @GetMapping("/{userId}")
    public ResponseEntity<List<WorkoutSession>> getWorkoutSessions(@PathVariable Long userId) {
        List<WorkoutSession> sessions = workoutSessionService.getSessionsByUserId(userId);
        if (sessions.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si no hay sesiones, devuelve un código 204
        }
        return ResponseEntity.ok(sessions); // Si hay sesiones, devuelve las sesiones
    }
}


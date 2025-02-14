package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutLog;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSession;
import com.aplicaciongimnasio.PuraEsencia.repository.ExerciseRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.WorkoutLogRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.WorkoutSessionRepository;
import com.aplicaciongimnasio.PuraEsencia.service.WorkoutLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/workout-logs")
@RequiredArgsConstructor
public class WorkoutLogController {

    @Autowired
    private WorkoutLogService workoutLogService;

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;

    // 🔹 Agregar un ejercicio a una sesión
    @PostMapping("/{sessionId}/{exerciseId}")
    public ResponseEntity<WorkoutLog> addExerciseToSession(
            @PathVariable Long sessionId,
            @PathVariable Long exerciseId,
            @RequestBody WorkoutLog logRequest) {

        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sesión no encontrada"));

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado"));

        WorkoutLog log = new WorkoutLog();
        log.setSession(session);
        log.setExercise(exercise);
        log.setRepetitions(logRequest.getRepetitions());
        log.setWeight(logRequest.getWeight());
        log.setNotes(logRequest.getNotes());

        WorkoutLog savedLog = workoutLogRepository.save(log);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    // 🔹 Obtener todos los registros de una sesión
    @GetMapping("/logs/{sessionId}")
    public ResponseEntity<List<WorkoutLog>> getLogsBySession(@PathVariable Long sessionId) {
        List<WorkoutLog> logs = workoutLogRepository.findBySession_UserId(sessionId);
        return ResponseEntity.ok(logs);
    }

    // 🔹 Obtener todos los registros de ejercicios de un cliente
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WorkoutLog>> getLogsByUser(@PathVariable Long userId) {
        List<WorkoutLog> logs = workoutLogRepository.findBySession_UserId(userId);
        return ResponseEntity.ok(logs);
    }

    // 🔹 Eliminar un registro de ejercicio
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteWorkoutLog(@PathVariable Long logId) {
        if (!workoutLogRepository.existsById(logId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro de ejercicio no encontrado");
        }
        workoutLogRepository.deleteById(logId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para obtener los logs de ejercicios de una sesión específica
    @GetMapping("/{sessionId}")
    public ResponseEntity<List<WorkoutLog>> getWorkoutLogs(@PathVariable Long sessionId) {
        List<WorkoutLog> workoutLogs = workoutLogService.getLogsBySessionId(sessionId);
        if (workoutLogs.isEmpty()) {
            return ResponseEntity.noContent().build(); // Si no hay logs, devuelve un código 204
        }
        return ResponseEntity.ok(workoutLogs); // Si hay logs, devuelve los logs
    }
}


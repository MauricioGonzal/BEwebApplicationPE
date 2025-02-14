package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutLog;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSession;
import com.aplicaciongimnasio.PuraEsencia.repository.ExerciseRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSession saveWorkoutSession(WorkoutSessionRequest request) {
        // Buscar usuario
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear nueva sesión de entrenamiento
        WorkoutSession session = new WorkoutSession();
        session.setUser(user);

        // Convertir logs del DTO a entidades
        List<WorkoutLog> logs = request.getLogs().stream().map(logRequest -> {
            Exercise exercise = exerciseRepository.findById(logRequest.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));

            WorkoutLog log = new WorkoutLog();
            log.setSession(session);
            log.setExercise(exercise);
            log.setRepetitions(logRequest.getRepetitions());
            log.setWeight(logRequest.getWeight());
            log.setNotes(logRequest.getNotes());

            return log;
        }).toList();

        return workoutSessionRepository.save(session);
    }

    // Obtiene las sesiones de un usuario dado el ID
    public List<WorkoutSession> getSessionsByUserId(Long userId) {
        return workoutSessionRepository.findByUserId(userId); // Llama al repositorio para obtener las sesiones
    }
}


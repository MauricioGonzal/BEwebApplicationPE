package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutResponse;
import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class WorkoutSessionService {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    @Autowired
    private WorkoutSetRepository workoutSetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Transactional
    public WorkoutSession saveWorkoutSession(WorkoutSessionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setDate(new Date());

        var id = workoutSessionRepository.save(session);
        System.out.println(id);

        WorkoutLog log = new WorkoutLog();
        log.setSession(session);
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
        log.setExercise(exercise);
        log.setNotes(request.getNote());

        WorkoutLog workoutLog = workoutLogRepository.save(log);

        //guardar sets

        List<WorkoutSet> sets = request.getSets().stream().map(logRequest -> {

            WorkoutSet workoutSet = new WorkoutSet();

            workoutSet.setRepetitions(logRequest.getRepetitions());

            workoutSet.setWorkoutLog(workoutLog);

            workoutSet.setWeight(logRequest.getWeight());

            workoutSetRepository.save(workoutSet);

            return workoutSet;
        }).toList();

        return id;
    }

    public List<WorkoutResponse> getSessionsByUserId(Long userId) {
        return getWorkoutResponses(userId);
    }

    @Transactional
    public List<WorkoutResponse> getWorkoutResponses(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Object[]> results = workoutSessionRepository.findSessionLogAndSets(user);
        Map<WorkoutLog, WorkoutResponse> logToResponseMap = new HashMap<>();

        for (Object[] result : results) {
            WorkoutSession session = (WorkoutSession) result[0];
            WorkoutLog log = (WorkoutLog) result[1];
            WorkoutSet set = (WorkoutSet) result[2];

            // Crear una nueva respuesta si no existe en el mapa
            WorkoutResponse response = logToResponseMap.computeIfAbsent(log, k -> {
                WorkoutResponse newResponse = new WorkoutResponse();
                newResponse.setWorkoutSession(session);
                newResponse.setWorkoutLog(log);
                newResponse.setSets(new ArrayList<>());
                return newResponse;
            });

            // Añadir el WorkoutSet a la lista de sets
            response.getSets().add(set);
        }

        // Convertir el mapa en una lista
        return new ArrayList<>(logToResponseMap.values());
    }
}


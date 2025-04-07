package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutLogResponse;
import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutResponse;
import com.aplicaciongimnasio.PuraEsencia.dto.WorkoutSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private RoutineSetRepository routineSetRepository;

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
        RoutineSet routineSet = routineSetRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Set no encontrado"));
        log.setRoutineSet(routineSet);
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
                WorkoutLogResponse workoutLogResponse = new WorkoutLogResponse();
                workoutLogResponse.setSession(log.getSession());
                List<Exercise> exerciseList = new ArrayList<>();
                ObjectMapper objectMapper = new ObjectMapper();

                String json = log.getRoutineSet().getExerciseIds(); // por ejemplo: "[1,2,3]"
                try {
                    List<Long> exerciseIds = objectMapper.readValue(json, new TypeReference<List<Long>>() {});
                    for(Long exerciseId : exerciseIds){
                        exerciseList.add(exerciseRepository.findById(exerciseId).orElseThrow(()-> new RuntimeException("No se encontro el ejercicio")));
                    }
                    workoutLogResponse.setExercisesRoutineSet(exerciseList);
                    workoutLogResponse.setNotes(log.getNotes());
                    WorkoutResponse newResponse = new WorkoutResponse();
                    newResponse.setWorkoutSession(session);

                    newResponse.setWorkoutLogResponse(workoutLogResponse);
                    newResponse.setSets(new ArrayList<>());
                    return newResponse;
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });

            // Añadir el WorkoutSet a la lista de sets
            response.getSets().add(set);
        }

        // Convertir el mapa en una lista
        return new ArrayList<>(logToResponseMap.values());
    }
}


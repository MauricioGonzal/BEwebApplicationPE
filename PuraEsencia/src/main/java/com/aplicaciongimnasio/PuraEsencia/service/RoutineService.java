package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.ExerciseDetails;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.ExerciseRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;


    public Routine createRoutine(RoutineRequest routineRequest) throws JsonProcessingException {
        Routine routine = new Routine();
        routine.setName(routineRequest.getTitle());
        routine.setDescription(routineRequest.getDescription());
        routine.setIsCustom(routineRequest.getIsCustom());

        // Asignar el Map<String, List<ExerciseDetails>> a exercisesByDay
        routine.setExercisesByDay(routineRequest.getExercises());

        // Guardar la rutina en la base de datos
        return routineRepository.save(routine);

    }

    // Obtener una rutina por ID
    public Routine getRoutineById(Long id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada con ID: " + id));
    }

    public Routine getRoutineByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getRoutine) // Obtener la rutina asociada
                .orElseThrow(() -> new RuntimeException("Usuario o rutina no encontrada"));
    }


    public Map<Long, Map<Integer, List<RoutineResponse>>> getRoutinesByCustom(Boolean custom) {
        List<Routine> routines = routineRepository.findAllByIsCustom(custom);
        Map<Long, Map<Integer, List<RoutineResponse>>> response = new HashMap<>();
        Map<Integer, List<RoutineResponse>> routineItem = new HashMap<>();

        for (Routine routine : routines) {
            if (routine.getExercisesByDay() != null) {
                for (Map.Entry<String, List<ExerciseDetails>> entry : routine.getExercisesByDay().entrySet()) {
                    Integer day = Integer.parseInt(entry.getKey()); // Convertir el día a número
                    List<ExerciseDetails> exerciseDetailsList = entry.getValue();
                    List<RoutineResponse> exerciseResponses = new ArrayList<>();

                    for (ExerciseDetails details : exerciseDetailsList) {
                        List<Exercise> exerciseList = new ArrayList<>();
                        for (Long exerciseId : details.getExerciseIds()) {
                            exerciseRepository.findById(exerciseId).ifPresent(exerciseList::add);
                        }
                        exerciseResponses.add(new RoutineResponse(
                                exerciseList,
                                details.getSeries(),
                                details.getRepetitions(),
                                details.getRest()
                        ));
                    }

                    routineItem.computeIfAbsent(day, k -> new ArrayList<>()).addAll(exerciseResponses);
                }
            }
            response.put(routine.getId(), routineItem);
        }

        return response;
    }
    public Routine updateRoutine(Long id, RoutineRequest routineRequest) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));

        // Actualizar datos de la rutina
        routine.setName(routineRequest.getTitle());
        routine.setDescription(routineRequest.getDescription());
        routine.setExercisesByDay(routineRequest.getExercises());

        return routineRepository.save(routine);
    }


}

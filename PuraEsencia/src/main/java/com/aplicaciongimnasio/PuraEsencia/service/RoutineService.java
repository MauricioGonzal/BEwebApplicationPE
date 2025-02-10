package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
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

    // Obtener una rutina por ID
    public List<Routine> getRoutinesByCustom(Boolean custom) {
        return routineRepository.findAllByIsCustom(custom);
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

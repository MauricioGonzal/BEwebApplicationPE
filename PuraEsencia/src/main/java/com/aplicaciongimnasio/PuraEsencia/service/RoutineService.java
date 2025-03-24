package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.EditRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.ExerciseRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineSetRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
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

    @Autowired
    private RoutineSetRepository routineSetRepository;


    public Routine createRoutine(RoutineRequest routineRequest) throws JsonProcessingException {
        Routine routine = new Routine();
        routine.setName(routineRequest.getTitle());
        routine.setDescription(routineRequest.getDescription());
        routine.setIsCustom(routineRequest.getIsCustom());
        routine = routineRepository.save(routine);
        // Crear un objeto ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // Convertir el List<Long> a JSON
        var exercises = routineRequest.getExercises();
        for (Map.Entry<String, List<ExerciseDetails>> entry : exercises.entrySet()) {
            for (ExerciseDetails item : entry.getValue()) {
                RoutineSet routineSet = new RoutineSet();
                routineSet.setRoutine(routine);
                String json = objectMapper.writeValueAsString(item.getExerciseIds());
                routineSet.setExerciseIds(json);
                routineSet.setDayNumber(Integer.parseInt(entry.getKey()));
                routineSet.setRest(item.getRest());
                routineSet.setRepetitions(item.getRepetitions());
                routineSet.setSeries(item.getSeries());
                routineSetRepository.save(routineSet);
            }
            System.out.println("Clave: " + entry.getKey() + ", Valor: " + entry.getValue());
        }
        return routine;

    }

    public List<RoutineSet> getRoutineById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada con ID: " + id));
        List<RoutineSet> routineSets = routineSetRepository.findByRoutine(routine);

        return routineSets;
    }

    public Routine getRoutineByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::getRoutine) // Obtener la rutina asociada
                .orElseThrow(() -> new RuntimeException("Usuario o rutina no encontrada"));
    }


    public List<RoutineSet> getRoutinesByCustom(Boolean custom) {
        var response = routineSetRepository.getAllFromNoCustomRoutine(custom);

        return response;
    }

    public List<RoutineSet> getRoutineSetByRoutine(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        var response = routineSetRepository.findByRoutine(routine);

        return response;
    }

    public Routine updateRoutine(Long id, EditRoutineRequest routineRequest) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));

        routine.setName(routineRequest.getName());
        routine.setDescription(routineRequest.getDescription());

        routineSetRepository.deleteByRoutine(routine);

        for (RoutineSet item : routineRequest.getExercises()) {
            RoutineSet routineSet = new RoutineSet();
            routineSet.setRoutine(routine);
            routineSet.setExerciseIds(item.getExerciseIds());
            routineSet.setRest(item.getRest());
            routineSet.setRepetitions(item.getRepetitions());
            routineSet.setSeries(item.getSeries());
            routineSet.setDayNumber(item.getDayNumber());
            routineSetRepository.save(routineSet);
        }

        return routineRepository.save(routine);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        routineSetRepository.deleteByRoutine(routine);
        routineRepository.deleteById(id);
        return true;
    }


}

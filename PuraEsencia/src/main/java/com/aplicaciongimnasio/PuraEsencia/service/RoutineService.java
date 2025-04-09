package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.EditRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineSetRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineSetResponse;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private RoutineSetSeriesRepository routineSetSeriesRepository;


    @Transactional
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
                routineSet.setSeries(item.getSeries());
                routineSetRepository.save(routineSet);

                // Guardar cada serie con sus repeticiones
                byte serieIndex = 1;
                for (Byte repsPerSerie : item.getRepetitionsPerSeries()) {
                    RoutineSetSeries routineSetSeries = new RoutineSetSeries();
                    routineSetSeries.setRoutineSet(routineSet);
                    routineSetSeries.setSeriesNumber(serieIndex); // número de serie (1, 2, 3...)
                    routineSetSeries.setRepetitions(repsPerSerie);
                    routineSetSeriesRepository.save(routineSetSeries); // asegurate de tener este repo
                    serieIndex++;
                }
            }
        }

        return routine;

    }

    public List<RoutineSetResponse> getRoutineById(Long id) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada con ID: " + id));

        List<RoutineSet> routineSets = routineSetRepository.findByRoutineAndIsActive(routine, true); // sin usar DTOs aquí

        List<RoutineSetResponse> responses = routineSets.stream()
                .map(rs -> {
                    List<Byte> series = routineSetSeriesRepository.getRepetitionsByRoutineSet(rs);
                    return new RoutineSetResponse(
                            rs.getRoutine(),
                            rs.getDayNumber(),
                            rs.getExerciseIds(),
                            rs.getSeries(),
                            rs.getRest(),
                            series
                    );
                }).collect(Collectors.toList());

        return responses;
    }


    public Routine getRoutineByEmail(String email) {
        return userRepository.findByEmailAndIsActive(email, true)
                .map(User::getRoutine) // Obtener la rutina asociada
                .orElseThrow(() -> new RuntimeException("Usuario o rutina no encontrada"));
    }


    public List<RoutineSet> getRoutinesByCustom(Boolean custom) {
        var response = routineSetRepository.getAllFromNoCustomRoutine(custom);

        return response;
    }

    public List<RoutineSet> getRoutineSetByRoutine(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        var response = routineSetRepository.findByRoutineAndIsActive(routine, true);

        return response;
    }

    @Transactional
    public Routine updateRoutine(Long id, EditRoutineRequest routineRequest) {
        Routine routine = routineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rutina no encontrada"));

        routine.setName(routineRequest.getName());
        routine.setDescription(routineRequest.getDescription());

        List <RoutineSet> routineSets = routineSetRepository.findByRoutineAndIsActive(routine, true);

        for(RoutineSet routineSet : routineSets){
            routineSet.setIsActive(false);
            routineSetRepository.save(routineSet);
            List<RoutineSetSeries>  routineSetSeries = routineSetSeriesRepository.findByRoutineSet(routineSet);
            for(RoutineSetSeries routineSetSeries1: routineSetSeries){
                routineSetSeries1.setIsActive(false);
                routineSetSeriesRepository.save(routineSetSeries1);
            }
        }

        for (RoutineSetRequest item : routineRequest.getExercises()) {
            RoutineSet routineSet = new RoutineSet();
            routineSet.setRoutine(routine);
            routineSet.setExerciseIds(item.getExerciseIds());
            routineSet.setRest(item.getRest());
            routineSet.setSeries(item.getSeries());
            routineSet.setDayNumber(item.getDayNumber());
            routineSetRepository.save(routineSet);
            // Guardar cada serie con sus repeticiones
            byte serieIndex = 1;
            for (String repsPerSerie : item.getRepetitionsPerSeries()) {
                RoutineSetSeries routineSetSeries = new RoutineSetSeries();
                routineSetSeries.setRoutineSet(routineSet);
                routineSetSeries.setSeriesNumber(serieIndex); // número de serie (1, 2, 3...)
                routineSetSeries.setRepetitions(Byte.parseByte(repsPerSerie));
                routineSetSeriesRepository.save(routineSetSeries); // asegurate de tener este repo
                serieIndex++;
            }
        }

        return routineRepository.save(routine);
    }

    @Transactional
    public boolean deleteById(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        List<User> users = userRepository.findByRoutine(routine);
        if(!routine.getIsCustom()){
            if(!users.isEmpty()) throw new RuntimeException("Error al eliminar. La rutina seleccionada esta asociada a uno o más usuarios");
        }
        else{
            if(users.size() != 1) throw new RuntimeException("ERROR. Contactar con soporte.");
            User user = users.getFirst();
            user.setRoutine(null);
            userRepository.save(user);
        }

        routineSetRepository.deleteByRoutine(routine);
        routineRepository.deleteById(id);
        return true;
    }

    @Transactional
    public boolean unassignRoutineToUser(Long id) {
        Routine routine = routineRepository.findById(id).orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        List<User> users = userRepository.findByRoutine(routine);
        if(users.size() != 1) throw new RuntimeException("ERROR. Contactar con soporte.");
        User user = users.getFirst();
        user.setRoutine(null);
        userRepository.save(user);
        return true;
    }




}

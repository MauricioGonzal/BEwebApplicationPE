package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.ExerciseRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.RoutineRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoutineService {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    // Crear una rutina con ejercicios
    @Transactional
    public Routine createRoutineWithExercises(RoutineRequest routineRequest) {
        // Crear la entidad Routine
        Routine routine = new Routine();
        routine.setName(routineRequest.getName());

        // Buscar los ejercicios por sus IDs
        Set<Exercise> exercises = new HashSet<>();
        for (Long exerciseId : routineRequest.getExerciseIds()) {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado con ID: " + exerciseId));
            exercises.add(exercise);
        }

        routine.setExercises(exercises); // Asignar los ejercicios a la rutina

        // Guardar la rutina
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

    @Transactional
    public Routine addExercisesToRoutine(Long routineId, Set<Long> exerciseIds) {
        // Buscar la rutina existente
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));

        // Buscar los ejercicios por sus IDs
        Set<Exercise> exercisesToAdd = new HashSet<>(exerciseRepository.findAllById(exerciseIds));

        if (exercisesToAdd.isEmpty()) {
            throw new RuntimeException("No se encontraron ejercicios con los IDs proporcionados");
        }

        // Agregar los ejercicios a la rutina existente
        routine.getExercises().addAll(exercisesToAdd);

        // Guardar y devolver la rutina actualizada
        return routineRepository.save(routine);
    }
}

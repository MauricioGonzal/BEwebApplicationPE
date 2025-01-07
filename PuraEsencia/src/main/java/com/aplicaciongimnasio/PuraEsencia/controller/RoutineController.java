package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.service.ExerciseService;
import com.aplicaciongimnasio.PuraEsencia.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private ExerciseService exerciseService;

    // Crear una rutina con ejercicios
    @PostMapping("/create")
    public Routine createRoutineWithExercises(@RequestBody Routine routine, @RequestParam List<Long> exerciseIds) {
        return routineService.createRoutineWithExercises(routine, exerciseIds);
    }

    // Obtener una rutina por ID
    @GetMapping("/{id}")
    public Routine getRoutineById(@PathVariable Long id) {
        return routineService.getRoutineById(id);
    }

    @PutMapping("/{routineId}/add-exercises")
    public ResponseEntity<Routine> addExercisesToRoutine(
            @PathVariable Long routineId,
            @RequestParam Set<Long> exerciseIds) {
        Routine updatedRoutine = routineService.addExercisesToRoutine(routineId, exerciseIds);
        return ResponseEntity.ok(updatedRoutine);
    }
}

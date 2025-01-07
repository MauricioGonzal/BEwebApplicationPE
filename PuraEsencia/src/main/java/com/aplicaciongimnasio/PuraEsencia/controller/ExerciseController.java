package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    @Autowired
    private ExerciseService exerciseService;

    // Crear un nuevo ejercicio
    @PostMapping("/create")
    public Exercise createExercise(@RequestBody Exercise exercise) {
        return exerciseService.createExercise(exercise);
    }

    // Obtener un ejercicio por ID
    @GetMapping("/{id}")
    public Exercise getExerciseById(@PathVariable Long id) {
        return exerciseService.getExerciseById(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteExercise(@PathVariable Long id) {
        boolean isDeleted = exerciseService.deleteExerciseById(id);
        if (isDeleted) {
            return ResponseEntity.ok("Ejercicio eliminado exitosamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ejercicio no encontrado");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Exercise> updateExercise(@PathVariable Long id, @RequestBody Exercise updatedExercise) {
        Exercise exercise = exerciseService.updateExercise(id, updatedExercise);
        return ResponseEntity.ok(exercise);
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.EditRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.RoutineSet;
import com.aplicaciongimnasio.PuraEsencia.service.ExerciseService;
import com.aplicaciongimnasio.PuraEsencia.service.RoutineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<Routine> createRoutine(@RequestBody RoutineRequest routineRequest) throws JsonProcessingException {
        Routine routine = routineService.createRoutine(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(routine);
    }

    @GetMapping("/id/{id}")
    public List<RoutineSet> getRoutineById(@PathVariable Long id) {
        return routineService.getRoutineById(id);
    }

    @GetMapping("/email/{email}")
    public Routine getRoutineByEmail(@PathVariable String email) {
        return routineService.getRoutineByEmail(email);
    }

    @GetMapping("/nocustom")
    public List<RoutineSet> getRoutinesByCustom() {
        return routineService.getRoutinesByCustom(false);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Routine> updateRoutine(
            @PathVariable Long id,
            @RequestBody EditRoutineRequest routineRequest) {
        Routine updatedRoutine = routineService.updateRoutine(id, routineRequest);
        return ResponseEntity.ok(updatedRoutine);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoutineById(@PathVariable Long id) {
        boolean isDeleted = routineService.deleteById(id);
        return ResponseEntity.ok("Rutina eliminada exitosamente");

    }

    @GetMapping("/routine-set/{id}")
    public List<RoutineSet> getRoutineSetsByRoutine(@PathVariable Long id) {
        return routineService.getRoutineSetByRoutine(id);
    }

}

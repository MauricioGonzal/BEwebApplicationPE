package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.EditRoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineSetResponse;
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
@RequestMapping("/api/routine")
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
    public ResponseEntity<List<RoutineSetResponse>> getRoutineById(@PathVariable Long id) {
        return ResponseEntity.ok(routineService.getRoutineById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Routine> getRoutineByEmail(@PathVariable String email) {
        return ResponseEntity.ok(routineService.getRoutineByEmail(email));
    }

    @GetMapping("/nocustom")
    public ResponseEntity<List<RoutineSet>> getRoutinesByCustom() {
        return ResponseEntity.ok(routineService.getRoutinesByCustom(false));
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

    @PutMapping("/unassign/{id}")
    public ResponseEntity<String> unassignToUser(@PathVariable Long id) {
        boolean isUnassign = routineService.unassignRoutineToUser(id);
        return ResponseEntity.ok("Rutina desasignada exitosamente");
    }

    @GetMapping("/routine-set/{id}")
    public ResponseEntity<List<RoutineSet>> getRoutineSetsByRoutine(@PathVariable Long id) {
        return ResponseEntity.ok(routineService.getRoutineSetByRoutine(id));
    }

}

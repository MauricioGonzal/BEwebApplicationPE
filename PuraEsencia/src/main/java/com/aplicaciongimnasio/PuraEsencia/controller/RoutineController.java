package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.RoutineResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.service.ExerciseService;
import com.aplicaciongimnasio.PuraEsencia.service.RoutineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        System.out.println("holaaaaaa" + routineRequest);
        Routine routine = routineService.createRoutine(routineRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(routine);
    }

    @GetMapping("/id/{id}")
    public Routine getRoutineById(@PathVariable Long id) {
        return routineService.getRoutineById(id);
    }

    @GetMapping("/email/{email}")
    public Routine getRoutineByEmail(@PathVariable String email) {
        return routineService.getRoutineByEmail(email);
    }

    @GetMapping("/nocustom")
    public Map<Long, Map<Integer, List<RoutineResponse>>> getRoutinesByCustom() {
        return routineService.getRoutinesByCustom(false);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Routine> updateRoutine(
            @PathVariable Long id,
            @RequestBody RoutineRequest routineRequest) {

        Routine updatedRoutine = routineService.updateRoutine(id, routineRequest);
        return ResponseEntity.ok(updatedRoutine);
    }


}

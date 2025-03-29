package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.ClassSessionRequest;
import com.aplicaciongimnasio.PuraEsencia.model.ClassSchedule;
import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import com.aplicaciongimnasio.PuraEsencia.service.ClassScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
public class ClassScheduleController {

    @Autowired
    private ClassScheduleService scheduleService;

    // Crear una nueva grilla semanal
    @PostMapping
    public ResponseEntity<ClassSchedule> createSchedule(@RequestBody ClassSchedule schedule) {
        return ResponseEntity.ok(scheduleService.createSchedule(schedule));
    }

    @PostMapping("/createByUser/{userId}")
    public ResponseEntity<ClassSchedule> createScheduleByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(scheduleService.createScheduleByUser(userId));
    }

    // Obtener todas las grillas
    @GetMapping
    public ResponseEntity<List<ClassSchedule>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    // Obtener una grilla por ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<ClassSchedule>> getScheduleById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    // Agregar una sesión de clase a una grilla
    @PostMapping("/{scheduleId}/sessions")
    public ResponseEntity<ClassSession> createSession(@PathVariable Long scheduleId, @RequestBody ClassSessionRequest classSessionRequest) {
        return ResponseEntity.ok(scheduleService.createSession(scheduleId, classSessionRequest));
    }

    // Obtener todas las sesiones de clases
    @GetMapping("/sessions")
    public ResponseEntity<List<ClassSession>> getAllSessions() {
        return ResponseEntity.ok(scheduleService.getAllSessions());
    }

    // Obtener las sesiones de una grilla específica
    @GetMapping("/{scheduleId}/sessions")
    public ResponseEntity<List<ClassSession>> getSessionsBySchedule(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getSessionsBySchedule(scheduleId));
    }

    // Agregar una sesión de clase a una grilla
    @GetMapping("/getByUser/{userId}")
    public ResponseEntity <ClassSchedule> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(scheduleService.getByUser(userId));
    }


}

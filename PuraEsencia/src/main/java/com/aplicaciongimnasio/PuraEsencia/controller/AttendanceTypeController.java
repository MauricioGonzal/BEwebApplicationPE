package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/attendance-type")
public class AttendanceTypeController {
    @Autowired
    private AttendanceTypeService attendanceTypeService;

    @GetMapping
    public ResponseEntity<List<AttendanceType>> getAll() {
        return ResponseEntity.ok(attendanceTypeService.getAll());
    }

    @PostMapping
    public ResponseEntity<AttendanceType> create(@RequestBody AttendanceType attendanceType) {
        return ResponseEntity.ok(attendanceTypeService.create(attendanceType));
    }
}

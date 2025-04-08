package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceTypeRequest;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/attendance-type")
public class AttendanceTypeController {
    @Autowired
    private AttendanceTypeService attendanceTypeService;

    @GetMapping
    public List<AttendanceType> getAll() {
        return attendanceTypeService.getAll();
    }

    @PostMapping
    public AttendanceType create(@RequestBody AttendanceType attendanceType) {
        return attendanceTypeService.create(attendanceType);
    }
}

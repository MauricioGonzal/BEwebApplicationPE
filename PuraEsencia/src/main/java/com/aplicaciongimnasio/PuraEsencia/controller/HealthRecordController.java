package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.HealthRecord;
import com.aplicaciongimnasio.PuraEsencia.service.HealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/health-record")
public class HealthRecordController {

    @Autowired
    private HealthRecordService healthRecordService;

    @PostMapping("/create/{userId}")
    public HealthRecord createHealthRecord(@RequestBody HealthRecord healthRecord, @PathVariable Long userId) {
        return healthRecordService.createHealthRecord(healthRecord, userId);
    }
}

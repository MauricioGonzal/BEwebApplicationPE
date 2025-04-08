package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Area;
import com.aplicaciongimnasio.PuraEsencia.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/area")
public class AreaController {
    @Autowired
    AreaService areaService;

    @GetMapping
    public List<Area> getAll() {
        return areaService.getAll();
    }

    @PostMapping
    public Area create(@RequestBody Area area) {
        return areaService.create(area);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Area>> getAreaByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(areaService.getAreaByUser(userId));
    }
}

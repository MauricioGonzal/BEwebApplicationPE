package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/class-type")
public class ClassTypeController {

    @Autowired
    ClassTypeService classTypeService;

    @GetMapping
    public ResponseEntity<List<ClassType>> getAll() {
        return ResponseEntity.ok(classTypeService.getAll());
    }

    @GetMapping("/onSchedule")
    public ResponseEntity<List<ClassType>> getAllOnSchedule() {
        return ResponseEntity.ok(classTypeService.getAllOnSchedule());
    }

    @PostMapping
    public ResponseEntity<ClassType> create(@RequestBody ClassType classType){
        return ResponseEntity.ok(classTypeService.create(classType));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Long id){
        return ResponseEntity.ok(classTypeService.delete(id));
    }
}

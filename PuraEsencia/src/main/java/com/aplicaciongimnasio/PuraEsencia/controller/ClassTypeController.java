package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/classTypes")
public class ClassTypeController {

    @Autowired
    ClassTypeService classTypeService;

    @GetMapping
    public List<ClassType> getAll() {
        return classTypeService.getAll();
    }

    @PostMapping
    public ClassType create(@RequestBody ClassType classType){
        return classTypeService.create(classType);
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id){
        return classTypeService.delete(id);
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSchedule;
import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.service.ClassScheduleService;
import com.aplicaciongimnasio.PuraEsencia.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

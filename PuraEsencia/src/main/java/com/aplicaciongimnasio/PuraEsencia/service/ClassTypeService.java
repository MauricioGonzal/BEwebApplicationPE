package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTypeService {

    @Autowired
    ClassTypeRepository classTypeRepository;

    public List<ClassType> getAll() {
        return classTypeRepository.findAll();
    }
}

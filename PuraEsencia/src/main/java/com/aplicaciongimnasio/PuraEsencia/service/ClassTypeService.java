package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassSessionRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTypeService {

    @Autowired
    ClassTypeRepository classTypeRepository;

    @Autowired
    ClassSessionRepository classSessionRepository;

    public List<ClassType> getAll() {
        return classTypeRepository.findAll();
    }

    public ClassType create(ClassType classType){
        return classTypeRepository.save(classType);
    }

    public Boolean delete(Long id){
        ClassType classType = classTypeRepository.findById(id).orElseThrow(() -> new RuntimeException("La clase no se encuentra"));
        List<ClassSession> classSessions = classSessionRepository.findByClassType(classType);
        if(!classSessions.isEmpty()) throw new RuntimeException("La clase que se quiere eliminar esta activa en la grilla de clases.");
        classTypeRepository.delete(classType);
        return true;
    }
}

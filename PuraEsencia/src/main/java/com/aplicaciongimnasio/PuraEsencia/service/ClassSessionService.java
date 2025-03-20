package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import com.aplicaciongimnasio.PuraEsencia.repository.ClassSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassSessionService {

    @Autowired
    private ClassSessionRepository sessionRepository;

    public Boolean delete(Long id){
        ClassSession classSession = sessionRepository.findById(id).orElseThrow(() -> new RuntimeException("La sesion no se encuentra"));
        sessionRepository.delete(classSession);
        return true;
    }

    public ClassSession update(Long id, ClassSession updatedSession){
        ClassSession classSession = sessionRepository.findById(id).orElseThrow(() -> new RuntimeException("La sesion no se encuentra"));
        classSession.setClassType(updatedSession.getClassType());
        classSession.setStartTime(updatedSession.getStartTime());
        classSession.setEndTime(updatedSession.getEndTime());
        classSession.setTeacher(updatedSession.getTeacher());
        sessionRepository.save(classSession);
        return classSession;
    }
}

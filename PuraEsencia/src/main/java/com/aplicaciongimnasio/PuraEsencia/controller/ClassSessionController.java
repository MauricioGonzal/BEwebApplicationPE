package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.RoutineRequest;
import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.service.ClassSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/class-session")
@CrossOrigin(origins = "http://localhost:3000")
public class ClassSessionController {

    @Autowired
    private ClassSessionService classSessionService;

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id){
        return classSessionService.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassSession> updateRoutine(
            @PathVariable Long id,
            @RequestBody ClassSession classSession) {
        ClassSession updatedSession = classSessionService.update(id, classSession);
        return ResponseEntity.ok(updatedSession);
    }
}

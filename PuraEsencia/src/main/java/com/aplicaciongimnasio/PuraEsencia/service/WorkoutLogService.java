package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.WorkoutLog;
import com.aplicaciongimnasio.PuraEsencia.repository.WorkoutLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkoutLogService {

    @Autowired
    private WorkoutLogRepository workoutLogRepository;

    public List<WorkoutLog> getLogsBySessionId(Long sessionId) {
        return workoutLogRepository.findBySessionId(sessionId);
    }
}
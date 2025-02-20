package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.HealthRecord;
import com.aplicaciongimnasio.PuraEsencia.repository.HealthRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HealthRecordService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private UserService userService;

    public HealthRecord createHealthRecord(HealthRecord healthRecord, Long userId) {
        var healthRecordCreated = healthRecordRepository.save(healthRecord);

        userService.assignHealthRecord(userId, healthRecordCreated);

        return healthRecordCreated;
    }
}

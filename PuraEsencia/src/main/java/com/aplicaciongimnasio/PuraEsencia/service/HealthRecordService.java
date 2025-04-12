package com.aplicaciongimnasio.PuraEsencia.service;

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

    public HealthRecord getById(Long id){
        return healthRecordRepository.findById(id).orElseThrow(() -> new RuntimeException("No se encontro la ficha de salud"));

    }

    public HealthRecord update(Long id, HealthRecord healthRecord){
        HealthRecord existingRecord = healthRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ficha no encontrada con id: " + id));

        existingRecord.setAge(healthRecord.getAge());
        existingRecord.setPreexistingConditions(healthRecord.getPreexistingConditions());
        existingRecord.setPreviousInjuries(healthRecord.getPreviousInjuries());
        existingRecord.setPreviousSurgeries(healthRecord.getPreviousSurgeries());
        existingRecord.setCurrentMedication(healthRecord.getCurrentMedication());
        existingRecord.setAllergies(healthRecord.getAllergies());

        return healthRecordRepository.save(existingRecord);
    }
}

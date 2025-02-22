package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceTypeService {

    @Autowired
    private AttendanceTypeRepository attendanceTypeRepository;

    public List<AttendanceType> getAll() {
        return attendanceTypeRepository.findAll();
    }
}

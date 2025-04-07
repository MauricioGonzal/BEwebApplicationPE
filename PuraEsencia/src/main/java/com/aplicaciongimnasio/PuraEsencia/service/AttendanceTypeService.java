package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceTypeRequest;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
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

    public AttendanceType create (AttendanceTypeRequest attendanceTypeRequest){
        if(!attendanceTypeRepository.findByName(attendanceTypeRequest.getName()).isEmpty()) throw new RuntimeException("Ya existe un tipo de asistencia con ese nombre");

        AttendanceType attendanceType = new AttendanceType();
        attendanceType.setName(attendanceTypeRequest.getName());

        return attendanceTypeRepository.save(attendanceType);
    }
}

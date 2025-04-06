package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceTypeRequest;
import com.aplicaciongimnasio.PuraEsencia.model.Area;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import com.aplicaciongimnasio.PuraEsencia.repository.AreaRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.AttendanceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    public List<Area> getAll() {
        return areaRepository.findAll();
    }

    public Area create (Area area){
        if(areaRepository.findByName(area.getName()) != null ) throw new RuntimeException("Ya existe un area con ese nombre");

        return areaRepository.save(area);
    }
}

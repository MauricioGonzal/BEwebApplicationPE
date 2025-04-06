package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Area;
import com.aplicaciongimnasio.PuraEsencia.model.MembershipType;
import com.aplicaciongimnasio.PuraEsencia.repository.AreaRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.MembershipTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipTypeService {
    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    public List<MembershipType> getAll() {
        return membershipTypeRepository.findAll();
    }

    public MembershipType create (MembershipType membershipType){
        if(membershipTypeRepository.findByName(membershipType.getName()) != null) throw new RuntimeException("Ya existe un area con ese nombre");

        return membershipTypeRepository.save(membershipType);
    }
}

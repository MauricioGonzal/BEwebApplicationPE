package com.aplicaciongimnasio.PuraEsencia.service;


import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.repository.MembershipRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    public Membership getById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada con ID: " + id));
    }
}

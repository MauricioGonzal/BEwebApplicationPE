package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.repository.CashClosureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashClosureService {

    @Autowired
    private CashClosureRepository cashClosureRepository;

    public List<CashClosure> getAll() {
        return cashClosureRepository.findByClosureType("daily");
    }
}

package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.repository.CashClosureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class CashClosureService {

    @Autowired
    private CashClosureRepository cashClosureRepository;

    public List<CashClosure> getAllByType(String type) {
        return cashClosureRepository.findByClosureType(type);
    }

    public List<CashClosure> getByDate(LocalDate date) {
        return cashClosureRepository.findByStartDate(date);
    }

}

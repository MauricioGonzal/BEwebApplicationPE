package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceListService {

    @Autowired
    PriceListRepository priceListRepository;
    // Crear un nuevo ejercicio
    public PriceList createPrice(PriceList priceList) {
        return priceListRepository.save(priceList);
    }

    public List<PriceList> getAllPriceList() {
        return priceListRepository.findAll();
    }
}

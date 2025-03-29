package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Gym;
import com.aplicaciongimnasio.PuraEsencia.repository.GymRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GymService {
    @Autowired
    private GymRepository gymRepository;

    public Boolean createGym(String name){
        if(!gymRepository.findByName(name).isEmpty()) throw new RuntimeException("Ya existe un gimnasio registrado con ese nombre");

        Gym gym = new Gym();
        gym.setName(name);
        gymRepository.save(gym);
        return true;
    }
}

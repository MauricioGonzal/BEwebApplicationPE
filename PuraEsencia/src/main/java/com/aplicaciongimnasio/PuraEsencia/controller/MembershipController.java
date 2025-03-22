package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/membership")
public class MembershipController {

    @Autowired
    private MembershipService membershipService;

    @GetMapping
    public List<Membership> getAll() {
        return membershipService.getAllMemberships();
    }

    @GetMapping("/{id}")
    public Membership getById(@PathVariable Long id) {
        return membershipService.getById(id);
    }
}

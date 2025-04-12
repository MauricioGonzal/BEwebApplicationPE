package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.MembershipType;
import com.aplicaciongimnasio.PuraEsencia.service.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/membership-type")
public class MembershipTypeController {
    @Autowired
    private MembershipTypeService membershipTypeService;

    @GetMapping
    public ResponseEntity<List<MembershipType>> getAll() {
        return ResponseEntity.ok(membershipTypeService.getAll());
    }

    @PostMapping
    public ResponseEntity<MembershipType> create(@RequestBody MembershipType membershipType) {
        return ResponseEntity.ok(membershipTypeService.create(membershipType));
    }
}

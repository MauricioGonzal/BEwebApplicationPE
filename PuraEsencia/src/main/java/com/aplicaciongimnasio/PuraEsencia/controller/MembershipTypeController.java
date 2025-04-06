package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.AttendanceTypeRequest;
import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.model.MembershipType;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceTypeService;
import com.aplicaciongimnasio.PuraEsencia.service.MembershipTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/membership-type")
public class MembershipTypeController {
    @Autowired
    private MembershipTypeService membershipTypeService;

    @GetMapping
    public List<MembershipType> getAll() {
        return membershipTypeService.getAll();
    }

    @PostMapping
    public MembershipType create(@RequestBody MembershipType membershipType) {
        return membershipTypeService.create(membershipType);
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.MembershipRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.MembershipResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/priceList")
    public List<MembershipResponse> getAllMembershipsAndPriceList() {
        return membershipService.getAllMembershipsAndPriceLists();
    }

    @GetMapping("/priceList/simples")
    public List<MembershipResponse> getAllSimpleMembershipsAndPriceList() {
        return membershipService.getAllSimpleMembershipsAndPriceLists();
    }

    @GetMapping("/{id}")
    public Membership getById(@PathVariable Long id) {
        return membershipService.getById(id);
    }

    @PostMapping("/create-membership-price")
    public ResponseEntity<?> createMembershipAndPrice(@RequestBody MembershipRequest membershipRequest){
        Boolean result = membershipService.createMembershipAndPrice(membershipRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete-with-price")
    public ResponseEntity<String> deleteMembership(@RequestBody MembershipResponse membershipResponse) {
        boolean isRemoved = membershipService.deleteMembershipWithPrice(membershipResponse);
        if (isRemoved) {
            return ResponseEntity.ok("Membresia eliminada con éxito");
        } else {
            return ResponseEntity.status(404).body("Membresía no encontrada");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MembershipResponse> update(@PathVariable Long id, @RequestBody MembershipRequest membershipRequest) {
        MembershipResponse membershipResponse = membershipService.update(id, membershipRequest);
        return ResponseEntity.ok(membershipResponse);
    }

}

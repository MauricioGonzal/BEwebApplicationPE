package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.service.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/pricelists")
public class PriceListController {
    @Autowired
    private PriceListService priceListService;

    @PostMapping
    public ResponseEntity<?> createPrice(@RequestBody PriceList priceList) {
            return ResponseEntity.ok(priceListService.createPrice(priceList));
    }

    @GetMapping
    public List<PriceList> getAllPriceList() {
        return priceListService.getAllPriceList();
    }

    @GetMapping("/payments")
    public List<PriceList> getAllForPayments() {
        return priceListService.getAllForPayments();
    }

    @PutMapping("/{id}/updateAmount")
    public ResponseEntity<?> updateAmount(@PathVariable Long id, @RequestBody Float newAmount) {
            return ResponseEntity.ok(priceListService.updateAmount(id, newAmount));
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.FixedExpenseRequest;
import com.aplicaciongimnasio.PuraEsencia.model.FixedExpense;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.service.FixedExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/fixed-expenses")
public class FixedExpenseController {
    @Autowired
    private FixedExpenseService fixedExpenseService;

    @PostMapping
    public ResponseEntity<FixedExpense> createFixedExpense(@RequestBody FixedExpenseRequest request) {
        FixedExpense expense = fixedExpenseService.createFixedExpense(request);
        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public List<FixedExpense> getAllPriceList() {
        return fixedExpenseService.getAll();
    }
}

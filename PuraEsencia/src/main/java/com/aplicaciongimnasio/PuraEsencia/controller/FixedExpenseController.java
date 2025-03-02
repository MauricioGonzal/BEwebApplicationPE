package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.FixedExpenseRequest;
import com.aplicaciongimnasio.PuraEsencia.model.FixedExpense;
import com.aplicaciongimnasio.PuraEsencia.service.FixedExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FixedExpenseRequest request) {
        Optional<FixedExpense> expense = fixedExpenseService.update(id, request);
        if(expense.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else{
            return ResponseEntity.ok(expense);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> logicDelete(@PathVariable Long id) {
        return ResponseEntity.ok(fixedExpenseService.logicDelete(id));
    }

    @GetMapping
    public List<FixedExpense> getAllFixedExpense() {
        return fixedExpenseService.getAll();
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Salary;
import com.aplicaciongimnasio.PuraEsencia.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    @Autowired
    private SalaryService salaryService;

    @PostMapping
    public ResponseEntity<Salary> createSalary(@RequestParam Long userId, @RequestParam float amount) {
        Salary salary = salaryService.createSalary(userId, amount);
        return ResponseEntity.ok(salary);
    }

    @GetMapping
    public ResponseEntity<List<Salary>> getActiveSalaries() {
        List<Salary> salary = salaryService.getActiveSalaries();
        return ResponseEntity.ok(salary);
    }

    @PutMapping("/{id}/updateAmount")
    public ResponseEntity<?> updateAmount(@PathVariable Long id, @RequestBody float newAmount) {
        try {
            return ResponseEntity.ok(salaryService.updateAmount(id, newAmount));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}


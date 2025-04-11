package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Salary;
import com.aplicaciongimnasio.PuraEsencia.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
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
        return ResponseEntity.ok(salaryService.updateAmount(id, newAmount));
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@PathVariable Long id){
        return salaryService.delete(id);
    }
}


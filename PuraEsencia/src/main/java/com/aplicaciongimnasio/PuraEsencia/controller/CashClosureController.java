package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.service.CashClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/cash-closure")
public class CashClosureController {

    @Autowired
    private CashClosureService cashClosureService;

    @GetMapping("/{typeClosure}")
    public ResponseEntity<List<CashClosure>> getAllClosures(@PathVariable String typeClosure) {
        return ResponseEntity.ok(cashClosureService.getAllByType(typeClosure));
    }

    @GetMapping("/getByDate")
    public ResponseEntity<List<CashClosure>> getAllDailyClosures(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(cashClosureService.getByDate(date));
    }

    @GetMapping("/getByMonthAndYear")
    public ResponseEntity<List<CashClosure>> getByMonthAndYear(
            @RequestParam int month,
            @RequestParam int year) {

        var closures=  cashClosureService.getByMonthAndYear(month, year);

        return ResponseEntity.ok(closures);
    }

    @GetMapping("/calculate/daily")
    public ResponseEntity<Map<String, Object>> calculateDailyCashClosure(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(cashClosureService.calculateCashClosure(1L));
    }

    @GetMapping("/calculate/monthly")
    public ResponseEntity<Map<String, Object>> calculateMonthlyCashClosure(@RequestParam Long month) {
        return ResponseEntity.ok(cashClosureService.calculateCashClosure(month));
    }

    @PostMapping("/dailyClosing")
    public ResponseEntity<?> closeDailyCashRegister() {
        try{
            return ResponseEntity.ok(cashClosureService.closeDailyCashRegister());
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }

    }

    @PostMapping("/monthlyClosing")
    public ResponseEntity<CashClosure> closeMonthlyCashRegister(@RequestParam Long month) {
        return ResponseEntity.ok(cashClosureService.closeMonthlyCashRegister(month));
    }


}

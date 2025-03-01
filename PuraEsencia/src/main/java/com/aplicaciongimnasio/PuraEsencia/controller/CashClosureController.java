package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.service.CashClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateCashClosure(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {

        return ResponseEntity.ok(cashClosureService.calculateCashClosure(startDate, endDate));
    }


}

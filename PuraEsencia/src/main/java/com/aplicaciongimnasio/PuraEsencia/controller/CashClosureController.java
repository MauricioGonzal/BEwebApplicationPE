package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.service.CashClosureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/cash-closure")
public class CashClosureController {

    @Autowired
    private CashClosureService cashClosureService;

    @GetMapping("/{typeClosure}")
    public List<CashClosure> getAllDailyClosures(@PathVariable String typeClosure) {
        return cashClosureService.getAllByType(typeClosure);
    }

    @GetMapping("/getByDate")
    public List<CashClosure> getAllDailyClosures(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return cashClosureService.getByDate(date);
    }

}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
import com.aplicaciongimnasio.PuraEsencia.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try{
            return ResponseEntity.ok(transactionService.saveTransaction(transaction));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", e.getMessage()));
        }

    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/total/{date}")
    public ResponseEntity<Double> getTotalByDate(@PathVariable String date) {
        LocalDate transactionDate = LocalDate.parse(date);
        return ResponseEntity.ok(transactionService.getTotalByDate(transactionDate));
    }

    @PostMapping("/close/{date}")
    public ResponseEntity<CashClosure> closeCashRegister(@PathVariable String date) {
        LocalDate closureDate = LocalDate.parse(date);
        return ResponseEntity.ok(transactionService.closeCashRegister(closureDate));
    }
}

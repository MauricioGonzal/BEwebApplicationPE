package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> makePayment(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        Integer month = (Integer) request.get("month");
        Integer year = (Integer) request.get("year");

        String response = paymentService.registerPayment(userId, amount, month, year);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getUserPayments(userId);
        return ResponseEntity.ok(payments);
    }
}


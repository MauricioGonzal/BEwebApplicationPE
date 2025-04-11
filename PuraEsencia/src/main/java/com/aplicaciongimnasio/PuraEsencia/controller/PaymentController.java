package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceService;
import com.aplicaciongimnasio.PuraEsencia.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getbystatus/{status}")
    public ResponseEntity<List<Payment>> getByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status.toUpperCase());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/getbystatus/transaction/{status}")
    public ResponseEntity<List<Map<String, ?>>> getByStatusWithTransaction(@PathVariable String status) {
        List<Map<String, ?>> payments = paymentService.getPaymentsWithTransactionByStatus(status.toUpperCase());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/isOutDueDate/{userId}")
    public ResponseEntity<Boolean> isOutDueDate(@PathVariable Long userId) {
        return ResponseEntity.ok(attendanceService.isOutOfDueDate(userId));
    }

    @PutMapping("updateDueDate/{userId}")
    public ResponseEntity<Boolean> updateDueDate(@PathVariable Long userId, @RequestBody Map<String, String> newDueDate){
        return ResponseEntity.ok(paymentService.updateDueDate(userId, newDueDate));
    }

    @GetMapping("/overduePayments/{userId}")
    public ResponseEntity<List<Payment>> getOverduePaymentsByUserId(@PathVariable Long userId) {
        List<Payment> payments = paymentService.getOverduePaymentsByUserId(userId);
        return ResponseEntity.ok(payments);
    }
}


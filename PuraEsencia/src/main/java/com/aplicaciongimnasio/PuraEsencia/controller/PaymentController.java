package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.service.AttendanceService;
import com.aplicaciongimnasio.PuraEsencia.service.PaymentService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AttendanceService attendanceService;

    /*@PostMapping
    public ResponseEntity<String> makePayment(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Float amount = request.get("amount").toString();
        Integer month = (Integer) request.get("month");
        Integer year = (Integer) request.get("year");

        String response = paymentService.registerPayment(userId, amount, month, year);
        return ResponseEntity.ok(response);
    }
*/
    @GetMapping("/getbystatus/{status}")
    public ResponseEntity<List<Payment>> getByStatus(@PathVariable String status) {
        List<Payment> payments = paymentService.getPaymentsByStatus(status.toUpperCase());
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
}


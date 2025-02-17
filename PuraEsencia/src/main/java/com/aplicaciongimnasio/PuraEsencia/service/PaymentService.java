package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public String registerPayment(Long userId, BigDecimal amount, Integer month, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si ya hay un pago para el mes y año
        List<Payment> existingPayments = paymentRepository.findByUserIdAndMonthAndYear(userId, month, year);
        if (!existingPayments.isEmpty()) {
            return "El usuario ya tiene un pago registrado para " + month + "/" + year;
        }

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setMonth(month);
        payment.setYear(year);
        payment.setStatus("PAGADO");

        paymentRepository.save(payment);
        return "Pago registrado correctamente.";
    }

    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }
}

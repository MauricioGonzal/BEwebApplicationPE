package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public String registerPayment(Long userId, Float amount, String status, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        /*// Verificar si ya hay un pago para el mes y año
        List<Payment> existingPayments = paymentRepository.findByUserIdAndMonthAndYear(userId);
        if (!existingPayments.isEmpty()) {
            return "El usuario ya tiene un pago registrado para ";
        }*/


        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentDate(date);
        payment.setStatus(status);
        payment.setDueDate(date.plusMonths(1));

        paymentRepository.save(payment);
        return "Pago registrado correctamente.";
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }
}

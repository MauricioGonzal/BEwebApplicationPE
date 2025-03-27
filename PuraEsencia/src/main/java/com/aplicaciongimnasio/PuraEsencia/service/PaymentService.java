package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    public String registerPayment(Long userId, Float amount, String status, LocalDate paymentDate, LocalDate dueDate, Membership membership, Transaction transaction
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentDate(paymentDate);
        payment.setStatus(status);
        payment.setDueDate(dueDate);
        payment.setMembership(membership);
        if(transaction != null){
            payment.setTransaction(transaction);
        }

        paymentRepository.save(payment);
        return "Pago registrado correctamente.";
    }

    public List<Payment> getPaymentsByStatus(String status) {
        return paymentRepository.findByStatus(status);
    }

    public List<Payment> getPaymentsByStatusAndUserId(String status, Long userId) {
        return paymentRepository.findByStatusAndUserId(status,userId);
    }

    public Payment getLastPayment(Long userId){
        return paymentRepository.findLastByUserId(userId);
    }

    public Boolean updateDueDate(Long userId, Map<String, String> newDueDate) {
        registerPayment(userId, 0f, "PENDIENTE", LocalDate.parse(newDueDate.get("dueDate")), LocalDate.parse(newDueDate.get("dueDate")).plusMonths(1), null,null);

        return true;
    }

    public List<Payment> getOverduePaymentsByUserId(Long userId) {
        return paymentRepository.findByStatusAndUserId("PENDIENTE",userId);
    }

    public List<Map<String, ?>> getPaymentsWithTransactionByStatus(String status) {
        return paymentRepository.findWithTransactionByStatus(status);
    }


}

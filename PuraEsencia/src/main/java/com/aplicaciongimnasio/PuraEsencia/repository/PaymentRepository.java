package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(String status);
    List<Payment> findByStatusAndUserId(String status, Long userId);
    Optional<Payment> findFirstByUserIdOrderByDueDateDesc(Long userId);
    Payment findLastByUserId(Long userId);
}

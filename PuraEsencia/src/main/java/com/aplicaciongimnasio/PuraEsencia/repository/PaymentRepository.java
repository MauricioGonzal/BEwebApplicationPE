package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(String status);
    List<Payment> findByStatusAndUserId(String status, Long userId);
    Optional<Payment> findFirstByUserIdOrderByDueDateDesc(Long userId);
    Payment findLastByUserId(Long userId);

    @Query("""
    SELECT p FROM Payment p
    WHERE p.paymentDate <= :currentDate
    AND p.dueDate >= :currentDate
    AND p.paymentDate = (
        SELECT MAX(p2.paymentDate) FROM Payment p2 
        WHERE p2.user.id = p.user.id 
        AND p2.paymentDate <= :currentDate
    )
""")
    List<Payment> findLatestActivePayments(@Param("currentDate") LocalDate currentDate);

    @Query("""
    SELECT p FROM Payment p
    WHERE p.paymentDate <= :currentDate
    AND p.user.id = :userId
    AND p.dueDate >= :currentDate
    AND p.paymentDate = (
        SELECT MAX(p2.paymentDate) FROM Payment p2 
        WHERE p2.user.id = p.user.id 
        AND p2.paymentDate <= :currentDate
    )
""")
    List<Payment> findLatestActivePaymentsByUser(@Param("currentDate") LocalDate currentDate, @Param("userId") Long userId);
}

package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatus(String status);
    List<Payment> findByStatusAndUserIdAndMembership(String status, Long userId, Membership membership);
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

    @Query("""
    SELECT p FROM Payment p
    WHERE p.paymentDate <= :currentDate
    AND p.membership = :membership
    AND p.user.id = :userId
    AND p.dueDate >= :currentDate
    AND p.paymentDate = (
        SELECT MAX(p2.paymentDate) FROM Payment p2 
        WHERE p2.user.id = p.user.id 
        AND p2.paymentDate <= :currentDate
    )
""")
    List<Payment> findActivePaymentsByUserAndMembership(@Param("currentDate") LocalDate currentDate, @Param("userId") Long userId, @Param("membership") Membership membership);

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
    List<Payment> findActualPayment(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT p as payment, t as transaction FROM Payment p JOIN Transaction t ON p.transaction = t WHERE p.status = :status")
    List<Map<String, ?>> findWithTransactionByStatus(@Param("status") String status);

    @Query("""
    SELECT p FROM Payment p
    WHERE p.paymentDate <= :currentDate
    AND p.membership.area.name = "Clases"
    AND p.user.id = :userId
    AND p.dueDate >= :currentDate
    AND p.paymentDate = (
        SELECT MAX(p2.paymentDate) FROM Payment p2 
        WHERE p2.user.id = p.user.id 
        AND p2.paymentDate <= :currentDate
    )
""")
    Payment findActiveClassesPayment(@Param("currentDate") LocalDate currentDate, @Param("userId") Long userId);

    @Query("""
    SELECT p FROM Payment p
    JOIN MembershipItem mi ON mi.membershipPrincipal = p.membership
    WHERE p.paymentDate <= :currentDate
    AND mi.membershipAssociated.area.name = "Clases"
    AND p.user.id = :userId
    AND p.dueDate >= :currentDate
    AND p.paymentDate = (
        SELECT MAX(p2.paymentDate) FROM Payment p2 
        WHERE p2.user.id = p.user.id 
        AND p2.paymentDate <= :currentDate
    )
""")
    Payment findActiveClassesCombinatedPayment(@Param("currentDate") LocalDate currentDate, @Param("userId") Long userId);

}

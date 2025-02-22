package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDate(LocalDate date);
    List<Transaction> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}

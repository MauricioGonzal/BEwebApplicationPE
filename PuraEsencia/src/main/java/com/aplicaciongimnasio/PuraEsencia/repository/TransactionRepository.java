package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.dto.TransactionResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDate(LocalDate date);
    List<Transaction> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    List<Transaction> findByDateBetweenAndTransactionCategory(LocalDateTime startOfDay, LocalDateTime endOfDay, TransactionCategory transactionCategory);
    List<Transaction> findByDateBetweenAndTransactionCategoryIn(LocalDateTime startDate, LocalDateTime endDate, List<TransactionCategory> transactionCategories);
    @Query("SELECT new com.aplicaciongimnasio.PuraEsencia.dto.TransactionResponse(" +
            "t.transactionCategory, t.paymentMethod, t.amount, t.date, t.comment, p, s) " +
            "FROM Transaction t " +
            "LEFT JOIN Payment p ON p.transaction.id = t.id " +
            "LEFT JOIN Sale s ON s.transaction.id = t.id " +  // Agregar JOIN con Sales
            "WHERE t.date BETWEEN :startOfDay AND :endOfDay " +
            "AND NOT EXISTS (SELECT c FROM CashClosure c WHERE c.closureType = 'daily' " +
            "AND FUNCTION('DATE', t.date) BETWEEN c.startDate AND c.endDate)")
    List<TransactionResponse> findTransactionsWithPaymentsAndSales(@Param("startOfDay") LocalDateTime startOfDay,
                                                                   @Param("endOfDay") LocalDateTime endOfDay);


    @Query("SELECT t FROM Transaction t WHERE DATE(t.date) = :today " +
            "AND NOT EXISTS (SELECT c FROM CashClosure c WHERE c.closureType = 'daily' AND :today BETWEEN c.startDate AND c.endDate)")
    List<Transaction> findUnclosedTransactions(@Param("today") LocalDate today);
}

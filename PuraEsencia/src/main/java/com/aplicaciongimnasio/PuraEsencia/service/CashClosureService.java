package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CashClosureService {

    @Autowired
    private CashClosureRepository cashClosureRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private FixedExpenseRepository fixedExpenseRepository;

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    public List<CashClosure> getAllByType(String type) {
        return cashClosureRepository.findByClosureType(type);
    }

    public List<CashClosure> getByDate(LocalDate date) {
        return cashClosureRepository.findByStartDate(date);
    }

    public List<CashClosure> getByMonthAndYear(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return cashClosureRepository.findByStartDateBetweenAndClosureType(startDate, endDate, "monthly");
    }

    public Map<String, Object> calculateCashClosure(LocalDate startDate, LocalDate endDate) {

        // Obtener el inicio del día (00:00:00)
        LocalDateTime startOfStartDate = startDate.atStartOfDay();

// Obtener el final del día (23:59:59.999999999)
        LocalDateTime endOfEndDate = endDate.atTime(23, 59, 59, 999999999);

        List<TransactionCategory> transactionCategoriesSales = List.of(
                transactionCategoryRepository.findByName("Musculacion"),
                transactionCategoryRepository.findByName("Clases"),
                transactionCategoryRepository.findByName("Producto")
        );
        List<Transaction> ingresos = transactionRepository.findByDateBetweenAndTransactionCategoryIn(startOfStartDate, endOfEndDate, transactionCategoriesSales);

        List<TransactionCategory> transactionCategoriesPayments = List.of(
                transactionCategoryRepository.findByName("Egreso")
        );

        List<Transaction> egresos = transactionRepository.findByDateBetweenAndTransactionCategoryIn (startOfStartDate, endOfEndDate, transactionCategoriesPayments);

        List<FixedExpense> fixedExpenses = fixedExpenseRepository.findByIsActive(true);
        List<Salary> salarios = salaryRepository.findByIsActive(true);

        Float totalIngresos = ingresos.stream()
                .map(Transaction::getAmount)
                .reduce(0f, Float::sum);

        Float totalEgresos = Math.abs(egresos.stream()
                .map(Transaction::getAmount)
                .reduce(0f, Float::sum));

        Float totalFixedExpenses = fixedExpenses.stream()
                .map(FixedExpense::getMonthlyAmount)
                .reduce(0f, Float::sum);

        Float totalSalarios = salarios.stream()
                .map(Salary::getAmount)
                .reduce(0f, Float::sum);

        Map<String, Object> response = new HashMap<>();
        response.put("totalIngresos", totalIngresos);
        response.put("totalEgresos", totalEgresos);
        response.put("totalFixedExpenses", totalFixedExpenses);
        response.put("totalSalarios", totalSalarios);
        response.put("ingresos", ingresos);
        response.put("egresos", egresos);
        response.put("fixedExpenses", fixedExpenses);
        response.put("salarios", salarios);

        return response;
    }

}

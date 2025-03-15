package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
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

    public Map<String, Object> calculateCashClosure(Long month) {
        int currentYear = LocalDate.now().getYear();

        LocalDate startDate = LocalDate.of(currentYear, month.intValue(), 1);
        LocalDateTime startOfStartDate = startDate.atStartOfDay();

        LocalDate endDate = YearMonth.of(currentYear, month.intValue()).atEndOfMonth();
        LocalDateTime endOfEndDate = endDate.atTime(23, 59, 59, 999999999);

        List<TransactionCategory> transactionCategoriesSales = List.of(
                transactionCategoryRepository.findByName("Musculacion").orElseThrow(() -> new RuntimeException("Transaction category not found")),
                transactionCategoryRepository.findByName("Clases").orElseThrow(() -> new RuntimeException("Transaction category not found")),
                transactionCategoryRepository.findByName("Producto").orElseThrow(() -> new RuntimeException("Transaction category not found"))
        );
        List<Transaction> sales = transactionRepository.findByDateBetweenAndTransactionCategoryIn(startOfStartDate, endOfEndDate, transactionCategoriesSales);

        List<TransactionCategory> transactionCategoriesPayments = List.of(
                transactionCategoryRepository.findByName("Egreso").orElseThrow(() -> new RuntimeException("Transaction category not found"))
        );

        List<Transaction> expenses = transactionRepository.findByDateBetweenAndTransactionCategoryIn (startOfStartDate, endOfEndDate, transactionCategoriesPayments);

        List<FixedExpense> fixedExpenses = fixedExpenseRepository.findByIsActive(true);
        List<Salary> salaries = salaryRepository.findByIsActive(true);

        Float totalSales = sales.stream()
                .map(Transaction::getAmount)
                .reduce(0f, Float::sum);

        Float totalExpenses = Math.abs(expenses.stream()
                .map(Transaction::getAmount)
                .reduce(0f, Float::sum));

        Float totalFixedExpenses = fixedExpenses.stream()
                .map(FixedExpense::getMonthlyAmount)
                .reduce(0f, Float::sum);

        Float totalSalarios = salaries.stream()
                .map(Salary::getAmount)
                .reduce(0f, Float::sum);

        Map<String, Object> response = new HashMap<>();
        response.put("totalIngresos", totalSales);
        response.put("totalEgresos", totalExpenses);
        response.put("totalFixedExpenses", totalFixedExpenses);
        response.put("totalSalarios", totalSalarios);
        response.put("ingresos", sales);
        response.put("egresos", expenses);
        response.put("fixedExpenses", fixedExpenses);
        response.put("salarios", salaries);

        return response;
    }

    /**
     * Realiza el cierre de caja sumando los ingresos y marcándolo como cerrado.
     */
    public CashClosure closeDailyCashRegister() {
        LocalDate today = LocalDate.now();

        if (cashClosureRepository.existsByStartDate(today)) {
            throw new RuntimeException("El cierre de caja para hoy ya fue registrado.");
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999); // Para cubrir todo el día

        TransactionCategory transactionCategory = transactionCategoryRepository.findByName("Egreso")
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> paymentsTransactions = transactionRepository.findByDateBetweenAndTransactionCategory(startOfDay, endOfDay, transactionCategory);

        double totalPayments = Math.abs(paymentsTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        List<TransactionCategory> transactionCategories = List.of(
                transactionCategoryRepository.findByName("Musculación").orElseThrow(() -> new RuntimeException("Transaction category musculación not found")),
                transactionCategoryRepository.findByName("Producto").orElseThrow(() -> new RuntimeException("Transaction category producto not found")),
                transactionCategoryRepository.findByName("Clases").orElseThrow(() -> new RuntimeException("Transaction category clases not found"))
        );

        List<Transaction> salesTransactions = transactionRepository.findByDateBetweenAndTransactionCategoryIn(startOfDay, endOfDay, transactionCategories);

        double totalSales = Math.abs(salesTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        double discrepancy = totalSales - totalPayments;

        CashClosure closure = new CashClosure(null, today, today, totalSales, totalPayments, discrepancy, "daily");
        return cashClosureRepository.save(closure);
    }

    public CashClosure closeMonthlyCashRegister(Long month) {
        int currentYear = LocalDate.now().getYear();

        LocalDate firstDayOfMonth = LocalDate.of(currentYear, month.intValue(), 1);

        LocalDate lastDayOfMonth = YearMonth.of(currentYear, month.intValue()).atEndOfMonth();

        if (cashClosureRepository.existsByStartDateAndEndDate(firstDayOfMonth, lastDayOfMonth)) {
            throw new RuntimeException("El cierre de caja para este mes ya fue registrado.");
        }

        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(23, 59, 59, 999999999); // Para cubrir todo el mes

        TransactionCategory transactionCategory = transactionCategoryRepository.findByName("Egreso")
                .orElseThrow(() -> new RuntimeException("Transaction category not found"));

        List<Transaction> paymentsTransactions = transactionRepository.findByDateBetweenAndTransactionCategory(startOfMonth, endOfMonth, transactionCategory);

        double totalPayments = Math.abs(paymentsTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        List<TransactionCategory> transactionCategories = List.of(
                transactionCategoryRepository.findByName("Musculación").orElseThrow(() -> new RuntimeException("Transaction category musculación not found")),
                transactionCategoryRepository.findByName("Producto").orElseThrow(() -> new RuntimeException("Transaction category producto not found")),
                transactionCategoryRepository.findByName("Clases").orElseThrow(() -> new RuntimeException("Transaction category clases not found"))
        );

        List<Transaction> salesTransactions = transactionRepository.findByDateBetweenAndTransactionCategoryIn(startOfMonth, endOfMonth, transactionCategories);

        double totalSales = Math.abs(salesTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        double discrepancy = totalSales - totalPayments;

        CashClosure closure = new CashClosure(null, firstDayOfMonth, lastDayOfMonth, totalSales, totalPayments, discrepancy, "monthly");
        return cashClosureRepository.save(closure);
    }

}

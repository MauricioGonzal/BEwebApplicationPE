package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CashClosureRepository cashClosureRepository;

    @Autowired
    private PriceListService priceListService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    /**
     * Registra una nueva transacción en la caja.
     */

    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        var transactionCategory = transactionCategoryRepository.findById(transaction.getTransactionCategory().getId())
                .orElseThrow(() -> new RuntimeException("Transaction Category not found"));
        transaction.setTransactionCategory(transactionCategory);

        var paymentMethod = paymentMethodRepository.findById(transaction.getPaymentMethod().getId())
                .orElseThrow(() -> new RuntimeException("Payment Method not found"));
        transaction.setPaymentMethod(paymentMethod);


        if(!transactionCategory.getName().equals("Egreso")){
            if(transaction.getMembership() != null){
                var membership = membershipRepository.findById(transaction.getMembership().getId())
                        .orElseThrow(() -> new RuntimeException("Membership not found"));
                transaction.setMembership(membership);
                var amount = priceListService.getAmountForTransaction(transactionCategory, paymentMethod, membership);
                transaction.setAmount(amount);
            }
            else{
                var amount = priceListService.getAmountForTransaction(transactionCategory, paymentMethod, null);
                transaction.setAmount(amount);
            }
        }
        else{
            transaction.setAmount(0 - transaction.getAmount());
        }

        if(transaction.getUser() != null){
            var user = userRepository.findById(transaction.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            transaction.setUser(user);
            List<Payment> overduePayments = paymentService.getPaymentsByStatusAndUserId("PENDIENTE", user.getId());
            if(!overduePayments.isEmpty()){
                Payment firstOverduePayment = overduePayments.getFirst();
                firstOverduePayment.setStatus("PAGADO");
                paymentRepository.save(firstOverduePayment);
            }
            else{
                List<Payment> payments = paymentService.getPaymentsByStatusAndUserId("PAGADO", user.getId());
                if(!payments.isEmpty()){
                    Payment lastPayment = payments.getLast();
                    if(lastPayment.getDueDate().isAfter(LocalDate.now())){
                        var membership = membershipRepository.findById(transaction.getMembership().getId())
                                .orElseThrow(() -> new RuntimeException("Membership not found"));
                        var maxDays = membership.getMaxDays();
                        if(maxDays != null && maxDays != 30){
                            paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusDays(maxDays));
                        }
                        else{
                            paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusMonths(1));
                        }
                    }
                }
                else{
                    paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO", LocalDate.now(), LocalDate.now().plusMonths(1));
                }
            }
        }
        return transactionRepository.save(transaction);
    }

    /**
     * Obtiene todas las transacciones registradas.
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Obtiene todas las transacciones de un día específico.
     */
    public List<Transaction> getByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // 2025-02-21T00:00:00
        LocalDateTime endOfDay = date.atTime(23, 59, 59); // 2025-02-21T23:59:59

        return transactionRepository.findByDateBetween(startOfDay, endOfDay);
    }

    /**
     * Calcula el total de ingresos de un día específico.
     */
    public double getTotalByDate(LocalDate date) {
        // Convertir LocalDate a LocalDateTime con hora 00:00:00
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999); // Para cubrir todo el día

        // Filtrar las transacciones que ocurren dentro del rango de este día
        List<Transaction> transactions = transactionRepository.findByDateBetween(startOfDay, endOfDay);

        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Realiza el cierre de caja sumando los ingresos y marcándolo como cerrado.
     */
    public CashClosure closeDailyCashRegister() {
        LocalDate today = LocalDate.now();

        // Verificar si el cierre ya existe
        if (cashClosureRepository.existsByStartDate(today)) {
            throw new RuntimeException("El cierre de caja para hoy ya fue registrado.");
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59, 999999999); // Para cubrir todo el día

        TransactionCategory transactionCategory = transactionCategoryRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> paymentsTransactions = transactionRepository.findByDateBetweenAndTransactionCategory(startOfDay, endOfDay, transactionCategory);

        double totalPayments = Math.abs(paymentsTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        List<TransactionCategory> transactionCategories = List.of(
                transactionCategoryRepository.findById(1L).orElseThrow(() -> new RuntimeException("Transaction category 1 not found")),
                transactionCategoryRepository.findById(2L).orElseThrow(() -> new RuntimeException("Transaction category 2 not found")),
                transactionCategoryRepository.findById(4L).orElseThrow(() -> new RuntimeException("Transaction category 3 not found"))
        );

        List<Transaction> salesTransactions = transactionRepository.findByDateBetweenAndTransactionCategoryIn(startOfDay, endOfDay, transactionCategories);

        double totalSales = Math.abs(salesTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum());

        double discrepancy = totalSales - totalPayments;

        // Guardar cierre de caja
        CashClosure closure = new CashClosure(null, today, today, totalSales, totalPayments, discrepancy, "daily");
        return cashClosureRepository.save(closure);
    }

    public CashClosure closeMonthlyCashRegister() {
            LocalDate today = LocalDate.now();
            LocalDate firstDayOfMonth = today.withDayOfMonth(1); // Primer día del mes
            LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth()); // Último día del mes

            // Verificar si el cierre mensual ya existe
            if (cashClosureRepository.existsByStartDateAndEndDate(firstDayOfMonth, lastDayOfMonth)) {
                throw new RuntimeException("El cierre de caja para este mes ya fue registrado.");
            }

            LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
            LocalDateTime endOfMonth = lastDayOfMonth.atTime(23, 59, 59, 999999999); // Para cubrir todo el mes

            // Obtener la categoría de transacción
            TransactionCategory transactionCategory = transactionCategoryRepository.findById(3L)
                    .orElseThrow(() -> new RuntimeException("Transaction category not found"));

            // Obtener todas las transacciones de pagos para el mes
            List<Transaction> paymentsTransactions = transactionRepository.findByDateBetweenAndTransactionCategory(startOfMonth, endOfMonth, transactionCategory);

            // Sumar los pagos, asegurándonos de usar BigDecimal
            double totalPayments = Math.abs(paymentsTransactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum());

            // Obtener todas las transacciones de ventas para el mes
            List<Transaction> salesTransactions = transactionRepository.findByDateBetweenAndTransactionCategory(startOfMonth, endOfMonth, transactionCategory);

            double totalSales = Math.abs(salesTransactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum());

            // Calcular la discrepancia
            double discrepancy = totalSales - totalPayments;

            // Guardar el cierre de caja mensual
            CashClosure closure = new CashClosure(null, firstDayOfMonth, lastDayOfMonth, totalSales, totalPayments, discrepancy, "monthly");
            return cashClosureRepository.save(closure);


    }

}

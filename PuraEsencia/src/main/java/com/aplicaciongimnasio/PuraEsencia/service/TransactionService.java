package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
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
            var amount = priceListService.getAmountForTransaction(transactionCategory, paymentMethod);
            transaction.setAmount(amount);
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
                        paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusMonths(1));
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
    public CashClosure closeCashRegister() {
        LocalDate today = LocalDate.now();

        // Verificar si el cierre ya existe
        if (cashClosureRepository.existsByDate(today)) {
            throw new RuntimeException("El cierre de caja para hoy ya fue registrado.");
        }

        // Calcular ingresos (puedes cambiar la lógica según tu necesidad)
        double totalIngresos = getTotalByDate(today);

        // Guardar cierre de caja
        CashClosure cierre = new CashClosure(null, today, totalIngresos);
        return cashClosureRepository.save(cierre);
    }

    public CashClosure monthlyCloseCashRegister() {
        LocalDate today = LocalDate.now();

        // Verificar si el cierre ya existe
        if (cashClosureRepository.existsByDate(today)) {
            throw new RuntimeException("El cierre de caja para hoy ya fue registrado.");
        }

        // Calcular ingresos (puedes cambiar la lógica según tu necesidad)
        double totalIngresos = getTotalByDate(today);

        // Guardar cierre de caja
        CashClosure cierre = new CashClosure(null, today, totalIngresos);
        return cashClosureRepository.save(cierre);
    }

}

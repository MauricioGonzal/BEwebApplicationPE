package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import com.aplicaciongimnasio.PuraEsencia.model.Transaction;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
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
            // Obtener el monto correcto de la lista de precios
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
            paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO");
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
        return transactionRepository.findByDate(date);
    }

    /**
     * Calcula el total de ingresos de un día específico.
     */
    public double getTotalByDate(LocalDate date) {
        List<Transaction> transactions = transactionRepository.findByDate(date);
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Realiza el cierre de caja sumando los ingresos y marcándolo como cerrado.
     */
    public CashClosure closeCashRegister(LocalDate date) {
        // Verificar si el cierre ya se realizó
        if (cashClosureRepository.existsByDate(date)) {
            throw new RuntimeException("El cierre de caja para esta fecha ya fue registrado.");
        }

        // Calcular ingresos del día
        double totalIngresos = getTotalByDate(date);

        // Guardar el cierre de caja
        CashClosure cierre = new CashClosure(date, totalIngresos);
        return cashClosureRepository.save(cierre);
    }
}

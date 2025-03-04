package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.TransactionRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.TransactionResponse;
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
    public Transaction saveTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        var transactionCategory = transactionCategoryRepository.findById(transactionRequest.getTransactionCategory().getId())
                .orElseThrow(() -> new RuntimeException("Transaction Category not found"));
        transaction.setTransactionCategory(transactionCategory);

        var paymentMethod = paymentMethodRepository.findById(transactionRequest.getPaymentMethod().getId())
                .orElseThrow(() -> new RuntimeException("Payment Method not found"));
        transaction.setPaymentMethod(paymentMethod);


        if(!transactionCategory.getName().equals("Egreso")){
            if(transactionRequest.getMembership() != null){
                var membership = membershipRepository.findById(transactionRequest.getMembership().getId())
                        .orElseThrow(() -> new RuntimeException("Membership not found"));
                var amount = priceListService.getAmountForTransaction(transactionCategory, paymentMethod, membership);
                transaction.setAmount(amount);
            }
            else{
                var amount = priceListService.getAmountForTransaction(transactionCategory, paymentMethod, null);
                transaction.setAmount(amount);
            }
        }
        else{
            transaction.setComment(transactionRequest.getComment());
            transaction.setAmount(0 - transactionRequest.getAmount());
        }

        transaction = transactionRepository.save(transaction);

        if(transactionRequest.getUser() != null){
            var user = userRepository.findById(transactionRequest.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Payment> overduePayments = paymentService.getPaymentsByStatusAndUserId("PENDIENTE", user.getId());
            if(!overduePayments.isEmpty()){
                Payment firstOverduePayment = overduePayments.getFirst();
                firstOverduePayment.setStatus("PAGADO");
                firstOverduePayment.setTransaction(transaction);
                paymentRepository.save(firstOverduePayment);
            }
            else{
                List<Payment> payments = paymentService.getPaymentsByStatusAndUserId("PAGADO", user.getId());
                if(!payments.isEmpty()){
                    Payment lastPayment = payments.getLast();
                    if(lastPayment.getDueDate().isAfter(LocalDate.now())){
                        var membership = membershipRepository.findById(transactionRequest.getMembership().getId())
                                .orElseThrow(() -> new RuntimeException("Membership not found"));
                        var maxDays = membership.getMaxDays();
                        if(maxDays != null && maxDays != 30){
                            paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusDays(maxDays), transactionRequest.getMembership(), transaction);
                        }
                        else{
                            paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusMonths(1), transactionRequest.getMembership(), transaction);
                        }
                    }
                }
                else{
                    paymentService.registerPayment(user.getId(), transaction.getAmount(), "PAGADO", LocalDate.now(), LocalDate.now().plusMonths(1), transactionRequest.getMembership(), transaction);
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
    public List<TransactionResponse> getByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay(); // 2025-02-21T00:00:00
        LocalDateTime endOfDay = date.atTime(23, 59, 59); // 2025-02-21T23:59:59

        return transactionRepository.findTransactionsWithPayments(startOfDay, endOfDay);
    }

    /**
     * Calcula el total de ingresos de un día específico.
     */
    public double getTotalByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59, 999999999); // Para cubrir todo el día

        List<Transaction> transactions = transactionRepository.findByDateBetween(startOfDay, endOfDay);

        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }


    public List<Transaction> getUnclosedTransactions() {
        LocalDate today = LocalDate.now();
        return transactionRepository.findUnclosedTransactions(today);
    }



}

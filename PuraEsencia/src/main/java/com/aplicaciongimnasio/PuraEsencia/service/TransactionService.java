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

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    /**
     * Registra una nueva transacción en la caja.
     */

    @Transactional
    public Transaction saveTransaction(TransactionRequest transactionRequest) {
        if (isTransactionWithinCashClosure()) {
            throw new IllegalStateException("Caja cerrada. No se pueden crear transacciones");
        }

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
                var amount = priceListService.getAmountForTransaction(null, paymentMethod, membership);
                transaction.setAmount(amount);
            }
            else{
                var amount = priceListService.getAmountForTransaction(transactionRequest.getProduct(), paymentMethod, null);
                transaction.setAmount(amount * transactionRequest.getQuantity());
            }
        }
        else{
            transaction.setComment(transactionRequest.getComment());
            transaction.setAmount(0 - transactionRequest.getAmount());
        }

        transaction = transactionRepository.save(transaction);

        if(transactionCategory.getName().equals("Producto")){
            ProductStock productStock = productStockRepository.findByIsActiveAndProduct(true, transactionRequest.getProduct());

            if(transactionRequest.getQuantity() > productStock.getStock()){
                throw new RuntimeException("No hay suficiente stock para realizar la venta");
            }
            Sale sale = new Sale();
            sale.setTransaction(transaction);
            sale.setProduct(transactionRequest.getProduct());
            sale.setQuantity(transactionRequest.getQuantity());
            sale = saleRepository.save(sale);

            productStock.setStock(productStock.getStock() - sale.getQuantity());
            productStockRepository.save(productStock);
        }



        if(transactionRequest.getUser() != null){
            var membership = membershipRepository.findById(transactionRequest.getMembership().getId())
                    .orElseThrow(() -> new RuntimeException("Membership not found"));
            var maxDays = membership.getMaxDays();
            var user = userRepository.findById(transactionRequest.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Payment> overduePayments = paymentService.getPaymentsByStatusAndUserIdAndMembership("PENDIENTE", user.getId(), transactionRequest.getMembership());
            if(!overduePayments.isEmpty()){
                Payment firstOverduePayment = overduePayments.getFirst();
                firstOverduePayment.setStatus("PAGADO");
                firstOverduePayment.setTransaction(transaction);
                firstOverduePayment.setMembership(transactionRequest.getMembership());
                paymentRepository.save(firstOverduePayment);
            }
            else{
                if(transactionRequest.getMembership().getArea().getName().equals("Musculacion")){
                    List<Payment> payments = paymentService.getPaymentsByStatusAndUserIdAndMembership("PAGADO", user.getId(), transactionRequest.getMembership());
                    if(!payments.isEmpty()){
                        Payment lastPayment = payments.getLast();
                        if(lastPayment.getDueDate().isAfter(LocalDate.now())){

                            if(maxDays != null && maxDays != 30){
                                paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusDays(maxDays), transactionRequest.getMembership(), transaction);
                            }
                            else{
                                paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), lastPayment.getDueDate().plusMonths(1), transactionRequest.getMembership(), transaction);
                            }
                        }
                    }
                    else{
                        if(maxDays != null && maxDays != 30){
                            paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), LocalDate.now().plusDays(maxDays), transactionRequest.getMembership(), transaction);
                        }
                        else{
                            paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), LocalDate.now().plusMonths(1), transactionRequest.getMembership(), transaction);
                        }
                    }
                }
                else if(transactionRequest.getMembership().getArea().getName().equals("Clases")){
                    paymentService.registerPayment(user.getId(), transactionRequest.getAmount(), "PAGADO", LocalDate.now(), LocalDate.now().plusMonths(1), transactionRequest.getMembership(), transaction);
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

        if (isTransactionWithinCashClosure()) {
            return List.of();
        }

        return transactionRepository.findTransactionsWithPaymentsAndSales(startOfDay, endOfDay);
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

    public boolean isTransactionWithinCashClosure() {
        LocalDate today = LocalDateTime.now().toLocalDate();
        return !cashClosureRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today).isEmpty();
    }

}

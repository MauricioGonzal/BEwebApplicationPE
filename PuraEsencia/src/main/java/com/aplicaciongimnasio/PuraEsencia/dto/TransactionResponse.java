package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Payment;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.Sale;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private TransactionCategory transactionCategory;
    private PaymentMethod paymentMethod;
    private Float amount;
    private LocalDateTime date = LocalDateTime.now();
    private String comment;
    private Payment payment;
    private Sale sale;
}

package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransactionRequest {
    private User user;
    private TransactionCategory transactionCategory;
    private PaymentMethod paymentMethod;
    private Float amount;
    private Membership membership;
    private String comment;
}

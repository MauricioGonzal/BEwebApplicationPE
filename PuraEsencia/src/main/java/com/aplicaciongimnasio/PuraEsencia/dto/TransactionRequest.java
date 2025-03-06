package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.*;
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
    private Product product;
    private Integer quantity;
}

package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    private String name;
    private Integer stock;
    private Float amount;
    private PaymentMethod paymentMethod;
    private TransactionCategory transactionCategory;
}

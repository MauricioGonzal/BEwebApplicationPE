package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el entrenador
    @ManyToOne
    @JoinColumn(name = "transaction_category_id") // Clave foránea que apunta al entrenador
    private TransactionCategory transactionCategory;

    // estara null si la categoria no es producto
    @ManyToOne
    @JoinColumn(name = "product_id") // Clave foránea que apunta al entrenador
    private Product product;

    @ManyToOne
    @JoinColumn(name = "payment_method_id") // Clave foránea que apunta al entrenador
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    private Float amount;

}

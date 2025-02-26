package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PriceList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_category_id")
    private TransactionCategory transactionCategory;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "payment_method_id")
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership;

    private Float amount;

    // Fecha de inicio de validez
    @Column(nullable = false)
    private LocalDate validFrom;

    // Fecha hasta cuando fue válido (puede ser null si es el actual)
    private LocalDate validUntil;

    // Indica si el precio sigue activo
    private Boolean isActive;

}

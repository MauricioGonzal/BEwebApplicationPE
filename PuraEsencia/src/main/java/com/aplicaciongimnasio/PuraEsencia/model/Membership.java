package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "membership")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre del plan (Ej: "Solo Gimnasio", "Gimnasio + Clases")

    @Column
    private Integer maxClasses; // Número de clases asignadas a este plan

    @Column
    private Integer maxDays; // Número de clases asignadas a este plan

    @ManyToOne
    @JoinColumn(name = "transaction_category_id")
    private TransactionCategory transactionCategory;
}


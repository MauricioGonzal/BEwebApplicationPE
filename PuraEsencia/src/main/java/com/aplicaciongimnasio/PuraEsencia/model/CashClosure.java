package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cash_closure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double totalSales;

    @Column(nullable = false)
    private Double totalPayments;

    @Column
    private Double totalFixedExpenses;

    @Column
    private Double totalSalaries;

    @Column(nullable = false)
    private Double discrepancy;

    @Column(nullable = false)
    private String closureType;
}



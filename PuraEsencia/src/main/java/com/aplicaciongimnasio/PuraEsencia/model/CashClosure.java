package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private LocalDate startDate;  // Start date for monthly closure, or the exact date for daily closure.

    @Column(nullable = false)
    private LocalDate endDate;    // End date for monthly closure, or same as startDate for daily closure.

    @Column(nullable = false)
    private Double totalSales;

    @Column(nullable = false)
    private Double totalPayments;

    @Column(nullable = false)
    private Double discrepancy;

    /*@ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;*/

    @Column(nullable = false)
    private String closureType;
}



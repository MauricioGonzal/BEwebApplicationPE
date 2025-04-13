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
@NoArgsConstructor
@AllArgsConstructor
public class FixedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private float monthlyAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    private Integer remainingInstallments;

    private Integer totalInstallments;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column
    private LocalDate createdAt = LocalDate.now();
}


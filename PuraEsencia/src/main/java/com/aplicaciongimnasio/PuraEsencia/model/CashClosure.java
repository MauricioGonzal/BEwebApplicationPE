package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cash_closures")
@Getter
@Setter
@NoArgsConstructor
public class CashClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private double totalIncome;

    public CashClosure(LocalDate date, double totalIncome) {
        this.date = date;
        this.totalIncome = totalIncome;
    }
}


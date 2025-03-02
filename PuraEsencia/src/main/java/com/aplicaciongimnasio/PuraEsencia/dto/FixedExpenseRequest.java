package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FixedExpenseRequest {
    private String name;
    private float monthlyAmount;
    private LocalDate startDate;
    private Integer remainingInstallments; // Puede ser null si es un gasto indefinido
}

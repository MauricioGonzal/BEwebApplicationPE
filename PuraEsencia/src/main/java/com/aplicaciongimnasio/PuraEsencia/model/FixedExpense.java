package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fixed_expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FixedExpense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Nombre del gasto

    @Column
    private float monthlyAmount; // Monto a pagar cada mes

    @Column(nullable = false)
    private LocalDate startDate; // Fecha de inicio del gasto

    private Integer remainingInstallments; // Número de cuotas restantes (NULL si es indefinido)

    @Column(nullable = false)
    private Boolean isActive = true; // Indica si el gasto sigue activo

    @Column
    private LocalDate createdAt = LocalDate.now(); // Fecha de creación
}


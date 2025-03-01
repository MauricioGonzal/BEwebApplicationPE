package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "salaries")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private float amount;

    // Fecha de inicio de validez
    @Column(nullable = false)
    private LocalDate validFrom;

    // Fecha hasta cuando fue válido (puede ser null si es el actual)
    private LocalDate validUntil;

    // Indica si el precio sigue activo
    private Boolean isActive;
}


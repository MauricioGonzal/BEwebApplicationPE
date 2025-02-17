package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount; // 💰 Monto pagado

    @Column(nullable = false)
    private LocalDate paymentDate; // 📅 Fecha de pago

    @Column(nullable = false)
    private Integer month; // 📆 Mes de la cuota

    @Column(nullable = false)
    private Integer year; // 📆 Año de la cuota

    @Column(nullable = false)
    private String status; // ✅ "PAGADO" o "PENDIENTE"
}

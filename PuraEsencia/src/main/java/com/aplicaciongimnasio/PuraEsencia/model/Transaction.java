package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user; // Puede ser null si es una venta sin usuario registrado

    private String type; // "MEMBERSHIP" o "SALE"
    private Double amount;
    private LocalDateTime date = LocalDateTime.now();
}

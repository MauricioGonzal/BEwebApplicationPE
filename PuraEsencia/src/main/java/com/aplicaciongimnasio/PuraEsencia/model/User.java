package com.aplicaciongimnasio.PuraEsencia.model;

import com.aplicaciongimnasio.PuraEsencia.security.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Relación Many-to-One con Routine
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = true) // El usuario puede o no tener una rutina
    private Routine routine;

    // Relación con el entrenador
    @ManyToOne
    @JoinColumn(name = "trainer_id") // Clave foránea que apunta al entrenador
    private User trainer;

    // Relación con ficha de salud
    @OneToOne
    @JoinColumn(name = "health_record_id") // Clave foránea que apunta al entrenador
    private HealthRecord healthRecord;

    @Column(columnDefinition = "TINYINT(1)") // MySQL
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "membership_id", nullable = true) // Puede ser null si no tiene membresía
    private Membership membership;


}


package com.aplicaciongimnasio.PuraEsencia.model;

import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
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

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = true)
    private Routine routine;

    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private User trainer;

    @OneToOne
    @JoinColumn(name = "health_record_id")
    private HealthRecord healthRecord;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isActive;
}


package com.aplicaciongimnasio.PuraEsencia.model;

import com.aplicaciongimnasio.PuraEsencia.security.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.usertype.UserType;

@Entity
@Getter
@Setter
public class User {
    //id, firstName, lastName, email, password, userType
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Relación Many-to-One con Routine
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = true) // El usuario puede o no tener una rutina
    private Routine routine;

    // Getters y setters
}


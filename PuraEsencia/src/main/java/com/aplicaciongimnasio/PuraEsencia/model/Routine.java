package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;  // Nombre de la rutina

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Exercise> exercises = new HashSet<>();  // Relación con los ejercicios

    //@ManyToOne
    //private User trainer;
}

package com.aplicaciongimnasio.PuraEsencia.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseDetails {
    private Long exerciseId;  // ID del ejercicio (relación con Exercise)
    private String name;
    private String description;
    private int series;       // Número de series
    private int repetitions;  // Número de repeticiones
}

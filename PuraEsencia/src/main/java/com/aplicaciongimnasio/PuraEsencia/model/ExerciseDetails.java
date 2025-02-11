package com.aplicaciongimnasio.PuraEsencia.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseDetails {
    private List<Long> exerciseIds;  // ID del ejercicio (relación con Exercise)
    private Byte series;       // Número de series
    private Byte repetitions;  // Número de repeticiones
    private Byte rest;
}

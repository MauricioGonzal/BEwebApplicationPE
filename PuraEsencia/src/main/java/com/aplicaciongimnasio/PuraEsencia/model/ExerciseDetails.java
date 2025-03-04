package com.aplicaciongimnasio.PuraEsencia.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseDetails {
    private List<Long> exerciseIds;
    private Byte series;
    private Byte repetitions;
    private Byte rest;
}

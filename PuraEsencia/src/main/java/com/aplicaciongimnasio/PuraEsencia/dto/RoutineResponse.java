package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutineResponse {
    private List<Exercise> exerciseList;
    private Byte series;
    private Byte repetitions;
    private Byte rest;

    public RoutineResponse(List<Exercise> exerciseList, Byte series, Byte repetitions, Byte rest) {
        this.exerciseList = exerciseList;
        this.series = series;
        this.repetitions = repetitions;
        this.rest = rest;
    }
}

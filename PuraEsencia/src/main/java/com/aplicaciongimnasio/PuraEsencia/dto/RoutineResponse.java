package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RoutineResponse {
    private List<Exercise> exerciseList;
    private Byte series;
    private Byte repetitions;
    private Byte rest;
}

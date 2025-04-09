package com.aplicaciongimnasio.PuraEsencia.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseDetails {
    private List<Long> exerciseIds;
    private Byte series; // podés mantenerlo si querés validación adicional
    private List<Byte> repetitionsPerSeries;
    private Byte rest; // si el descanso es igual para todas las series
}

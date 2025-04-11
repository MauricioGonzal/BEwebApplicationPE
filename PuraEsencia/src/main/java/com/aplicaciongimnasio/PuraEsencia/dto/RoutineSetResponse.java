package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoutineSetResponse {

    private Routine routine;

    private int dayNumber;

    private String exerciseIds;  // JSON de IDs de ejercicios (combinados)

    private int series;
    private int rest;

    private List<Byte> repetitionsPerSeries;
}

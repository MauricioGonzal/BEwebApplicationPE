package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineSetRequest {
    private Routine routine;

    private int dayNumber;

    private String exerciseIds;  // JSON de IDs de ejercicios (combinados)

    private int series;
    private int rest;

    private List<String> repetitionsPerSeries;

    private Boolean isActive=true;
}

package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSet;
import lombok.Data;

import java.util.List;

@Data
public class WorkoutSessionRequest {
    private Long userId;
    private Long exerciseId;
    private List<WorkoutSet> sets;
    private String note;
}

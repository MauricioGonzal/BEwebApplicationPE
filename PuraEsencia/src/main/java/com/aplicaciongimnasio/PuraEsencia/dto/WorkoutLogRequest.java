package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public  class WorkoutLogRequest {
    private Long exerciseId;
    private int repetitions;
    private double weight;
    private String notes;
}
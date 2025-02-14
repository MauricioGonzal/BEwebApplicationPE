package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Data;

import java.util.List;

@Data
public class WorkoutSessionRequest {
    private Long userId;
    private List<WorkoutLogRequest> logs;
}

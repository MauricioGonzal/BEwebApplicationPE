package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRoutineRequest {
    private Long trainerId;
    private Long userId;
    private Long routineId;
}

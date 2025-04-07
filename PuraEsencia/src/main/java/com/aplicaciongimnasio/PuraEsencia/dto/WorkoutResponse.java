package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.WorkoutLog;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSession;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public  class WorkoutResponse {
    private WorkoutSession workoutSession;
    private WorkoutLogResponse workoutLogResponse;
    private List<WorkoutSet> sets;
}
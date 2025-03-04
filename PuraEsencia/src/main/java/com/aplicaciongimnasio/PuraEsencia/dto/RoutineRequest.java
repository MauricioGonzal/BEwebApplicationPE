package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.ExerciseDetails;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RoutineRequest {
    private String title;
    private String description;
    private Boolean isCustom;
    private Map<String, List<ExerciseDetails>> exercises;
}

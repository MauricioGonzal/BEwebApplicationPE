package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditRoutineRequest {
    private String name;
    private String description;
    private Boolean isCustom;
    private List<RoutineSetRequest> exercises;  // JSON de IDs de ejercicios (combinados)

}

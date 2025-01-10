package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutineRequest {
    private String name;
    private List<Long> exerciseIds;
}

package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import com.aplicaciongimnasio.PuraEsencia.model.enums.DayOfWeek;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ClassSessionRequest {
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private ClassType classType;

    /*private Long instructorId;
    private Integer maxCapacity;*/
}

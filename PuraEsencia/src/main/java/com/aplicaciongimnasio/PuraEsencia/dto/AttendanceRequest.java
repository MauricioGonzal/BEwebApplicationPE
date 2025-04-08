package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRequest {
    private Long userId;
    private Long classTypeId;
}

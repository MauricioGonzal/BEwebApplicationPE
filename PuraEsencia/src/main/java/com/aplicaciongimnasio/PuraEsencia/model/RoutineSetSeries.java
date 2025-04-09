package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoutineSetSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Byte seriesNumber;

    private Byte repetitions;

    // opcional
    // private Byte rest;

    @ManyToOne
    @JoinColumn(name = "routine_set_id")
    private RoutineSet routineSet;

    private Boolean isActive=true;
}


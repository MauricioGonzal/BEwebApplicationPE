package com.aplicaciongimnasio.PuraEsencia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    private int dayNumber;

    @Column(columnDefinition = "TEXT")
    private String exerciseIds;  // JSON de IDs de ejercicios (combinados)

    private int series;
    private int rest;

    private Boolean isActive=true;

}


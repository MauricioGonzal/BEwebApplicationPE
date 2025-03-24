package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private int repetitions;
    private int rest;
}


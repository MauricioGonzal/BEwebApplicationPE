package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workout_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private WorkoutSession session;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;
    
    private int series;
    private int repetitions;
    private double weight;
    private String notes;

}
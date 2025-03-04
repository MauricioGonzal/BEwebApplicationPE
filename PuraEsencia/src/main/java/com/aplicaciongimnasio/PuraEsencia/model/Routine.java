package com.aplicaciongimnasio.PuraEsencia.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(columnDefinition = "TINYINT(1)")
    private Boolean isCustom;

    @Column(columnDefinition = "TEXT")
    private String exercisesJson;

    @Transient
    private Map<String, List<ExerciseDetails>> exercisesByDay;

    @PostLoad
    private void loadJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.exercisesByDay = mapper.readValue(exercisesJson, new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PrePersist
    @PreUpdate
    private void saveJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.exercisesJson = mapper.writeValueAsString(exercisesByDay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setExercisesByDay(Map<String, List<ExerciseDetails>> exercisesByDay) {
        this.exercisesByDay = exercisesByDay;
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.exercisesJson = mapper.writeValueAsString(exercisesByDay);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

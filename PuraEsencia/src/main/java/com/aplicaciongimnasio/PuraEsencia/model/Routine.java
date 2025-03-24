package com.aplicaciongimnasio.PuraEsencia.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


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
}

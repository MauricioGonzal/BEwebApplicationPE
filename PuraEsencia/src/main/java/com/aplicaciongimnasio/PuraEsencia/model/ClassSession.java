package com.aplicaciongimnasio.PuraEsencia.model;

import com.aplicaciongimnasio.PuraEsencia.model.enums.DayOfWeek;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
public class ClassSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ClassSchedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "class_type_id", nullable = false)
    private ClassType classType;

    /*private Long instructorId;
    private Integer maxCapacity;*/
}


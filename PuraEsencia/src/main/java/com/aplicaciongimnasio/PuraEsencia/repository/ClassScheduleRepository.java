package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
}

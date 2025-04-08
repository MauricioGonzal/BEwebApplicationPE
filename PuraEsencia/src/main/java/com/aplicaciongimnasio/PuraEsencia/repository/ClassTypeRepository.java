package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
    List<ClassType> findByIsActive(Boolean isActive);
    @Query("SELECT ct FROM ClassType ct JOIN ClassSession cs ON ct = cs.classType WHERE ct.isActive = true")
    List<ClassType> getAllOnSchedule();
}

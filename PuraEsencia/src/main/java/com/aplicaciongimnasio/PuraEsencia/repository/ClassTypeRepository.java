package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
    List<ClassType> findByIsActive(Boolean isActive);
}

package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Long> {
    AttendanceType getById(Long id);
}

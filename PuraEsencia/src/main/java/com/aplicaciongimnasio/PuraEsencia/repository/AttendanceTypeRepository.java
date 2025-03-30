package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.AttendanceType;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Long> {
    AttendanceType getById(Long id);
    List<AttendanceType> findByName(String name);
    List<AttendanceType> findByRoleAccepted(Role role);
}

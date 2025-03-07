package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    List<ClassSession> findByScheduleId(Long scheduleId);
}

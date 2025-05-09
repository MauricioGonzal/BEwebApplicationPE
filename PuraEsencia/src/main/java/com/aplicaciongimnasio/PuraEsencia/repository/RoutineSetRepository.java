package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Routine;
import com.aplicaciongimnasio.PuraEsencia.model.RoutineSet;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineSetRepository extends JpaRepository<RoutineSet, Long> {
    List<RoutineSet> findByRoutineAndIsActive(Routine routine, Boolean isActive);
    @Modifying
    @Transactional
    @Query("DELETE FROM RoutineSet rs WHERE rs.routine = :routine")
    void deleteByRoutine(@Param("routine") Routine routine);
    @Query("Select rs from RoutineSet rs where rs.routine.isCustom = :isCustom")
    List<RoutineSet> getAllFromNoCustomRoutine(@Param("isCustom") Boolean isCustom);
}

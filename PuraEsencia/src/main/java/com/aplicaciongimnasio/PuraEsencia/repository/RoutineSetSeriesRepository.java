package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.RoutineSet;
import com.aplicaciongimnasio.PuraEsencia.model.RoutineSetSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineSetSeriesRepository extends JpaRepository<RoutineSetSeries, Long> {
    List<RoutineSetSeries> findByRoutineSet(RoutineSet routineSet);
    List<RoutineSetSeries> deleteAllByRoutineSet(RoutineSet routineSet);

    @Query("SELECT rss.repetitions FROM RoutineSetSeries rss WHERE rss.routineSet = :routineSet")
    List<Byte> getRepetitionsByRoutineSet(@Param("routineSet") RoutineSet routineSet);

}

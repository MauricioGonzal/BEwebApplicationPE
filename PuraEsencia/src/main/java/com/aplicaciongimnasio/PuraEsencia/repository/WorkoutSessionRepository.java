package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    List<WorkoutSession> findByUserId(Long userId);

    @Query("SELECT ws, wl, wset " +
            "from WorkoutSession ws " +
            "JOIN WorkoutLog wl ON wl.session = ws " +
            "JOIN WorkoutSet wset ON wset.workoutLog = wl " +
            "WHERE ws.user = :user")
    List<Object[]> findSessionLogAndSets(@Param("user") User user);
}

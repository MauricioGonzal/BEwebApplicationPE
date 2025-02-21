package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUserIdAndDate(Long userId, java.time.LocalDate date);
    Attendance findFirstByUserId(Long userId);
    boolean existsByUserIdAndDate(Long userId, LocalDate today);
    @Query("SELECT a.user.id FROM Attendance a WHERE a.date = :today")
    List<Long> findUserIdsByDate(@Param("today") LocalDate today);
}

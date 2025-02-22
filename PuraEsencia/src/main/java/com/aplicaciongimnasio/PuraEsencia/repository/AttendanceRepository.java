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
    List<Attendance> findByDate(LocalDate today);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.user.id = :userId AND MONTH(a.date) = MONTH(CURRENT_DATE) AND YEAR(a.date) = YEAR(CURRENT_DATE)")
    long countAttendancesInCurrentMonth(@Param("userId") Long userId);

    List<Attendance> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}

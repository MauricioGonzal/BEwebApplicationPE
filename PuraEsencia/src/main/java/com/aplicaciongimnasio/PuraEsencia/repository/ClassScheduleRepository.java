package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.ClassSchedule;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, Long> {
    @Query("SELECT cs FROM ClassSchedule cs JOIN Gym g ON cs.gym = g JOIN User u ON u.gym = g WHERE u = :user")
    List<ClassSchedule> getByAdmin(User user);
}

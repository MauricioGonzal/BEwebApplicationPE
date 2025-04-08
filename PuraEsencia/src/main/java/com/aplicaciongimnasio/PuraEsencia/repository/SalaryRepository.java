package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByUserIdAndValidUntilIsNull(Long employeeId);
    List<Salary> findByValidUntilIsNull();
    List<Salary> findByIsActive(Boolean isActive);
}
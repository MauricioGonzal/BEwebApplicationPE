package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface CashClosureRepository extends JpaRepository<CashClosure, Long> {
    boolean existsByStartDate(LocalDate date);
    boolean existsByStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
    List<CashClosure> findByClosureType(String closureType);
    List<CashClosure> findByStartDate(LocalDate date);
    List<CashClosure> findByStartDateBetweenAndClosureType(LocalDate start, LocalDate end, String closureType);
}

package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.CashClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface CashClosureRepository extends JpaRepository<CashClosure, Long> {
    boolean existsByDate(LocalDate date);
}

package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.Salary;
import com.aplicaciongimnasio.PuraEsencia.model.User;
import com.aplicaciongimnasio.PuraEsencia.repository.SalaryRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryService {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Salary createSalary(Long userId, BigDecimal amount) {
        var employee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Cerrar salario anterior si existe
        salaryRepository.findByUserIdAndValidUntilIsNull(userId)
                .forEach(s -> {
                    s.setValidUntil(LocalDate.now());
                    salaryRepository.save(s);
                });

        // Crear nuevo salario
        Salary salary = new Salary();
        salary.setUser(employee);
        salary.setAmount(amount);
        salary.setValidFrom(LocalDate.now());
        salary.setIsActive(true);

        return salaryRepository.save(salary);
    }

    public List<Salary> getActiveSalaries(){
        return salaryRepository.findByValidUntilIsNull();
    }

    public Salary updateAmount(Long id, BigDecimal newAmount) {
        // 1. Buscar el registro actual
        Optional<Salary> existingSalaryOpt = salaryRepository.findById(id);
        if (existingSalaryOpt.isEmpty()) {
            throw new RuntimeException("No salary found with id: " + id);
        }

        Salary existingSalary = existingSalaryOpt.get();

        // 2. Desactivar el registro actual
        existingSalary.setIsActive(false);
        existingSalary.setValidUntil(LocalDate.now());
        salaryRepository.save(existingSalary);

        // 3. Crear un nuevo registro con el nuevo monto y activo
        Salary newSalary = new Salary();
        newSalary.setUser(existingSalary.getUser());
        newSalary.setAmount(newAmount);
        newSalary.setValidFrom(LocalDate.now());
        newSalary.setIsActive(true);
        newSalary.setValidUntil(null); // No tiene fecha de fin porque es el actual

        return salaryRepository.save(newSalary);
    }
}


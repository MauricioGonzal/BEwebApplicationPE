package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Salary;
import com.aplicaciongimnasio.PuraEsencia.repository.SalaryRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    public Salary createSalary(Long userId, float amount) {
        var employee = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        salaryRepository.findByUserIdAndValidUntilIsNull(userId)
                .forEach(s -> {
                    s.setValidUntil(LocalDate.now());
                    salaryRepository.save(s);
                });

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

    public Salary updateAmount(Long id, float newAmount) {
        Optional<Salary> existingSalaryOpt = salaryRepository.findById(id);
        if (existingSalaryOpt.isEmpty()) {
            throw new RuntimeException("No salary found with id: " + id);
        }

        Salary existingSalary = existingSalaryOpt.get();

        existingSalary.setIsActive(false);
        existingSalary.setValidUntil(LocalDate.now());
        salaryRepository.save(existingSalary);

        Salary newSalary = new Salary();
        newSalary.setUser(existingSalary.getUser());
        newSalary.setAmount(newAmount);
        newSalary.setValidFrom(LocalDate.now());
        newSalary.setIsActive(true);
        newSalary.setValidUntil(null); // No tiene fecha de fin porque es el actual

        return salaryRepository.save(newSalary);
    }

    public Boolean delete(Long id){
        Salary salary = salaryRepository.findById(id).orElseThrow(() -> new RuntimeException("El salario no se encuentra"));
        salary.setIsActive(false);
        salary.setValidUntil(LocalDate.now());
        salaryRepository.save(salary);
        return true;
    }
}


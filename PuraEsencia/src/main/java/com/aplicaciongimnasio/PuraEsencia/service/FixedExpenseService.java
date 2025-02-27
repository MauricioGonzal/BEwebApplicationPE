package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.FixedExpenseRequest;
import com.aplicaciongimnasio.PuraEsencia.model.FixedExpense;
import com.aplicaciongimnasio.PuraEsencia.repository.FixedExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixedExpenseService {
    @Autowired
    private FixedExpenseRepository fixedExpenseRepository;

    public FixedExpense createFixedExpense(FixedExpenseRequest request) {
        FixedExpense expense = new FixedExpense();
        expense.setName(request.getName());
        expense.setTotalAmount(request.getTotalAmount());
        expense.setMonthlyAmount(request.getMonthlyAmount());
        expense.setStartDate(request.getStartDate());
        expense.setRemainingInstallments(request.getRemainingInstallments());
        expense.setIsActive(true);

        return fixedExpenseRepository.save(expense);
    }

    public List<FixedExpense> getAll() {
        return fixedExpenseRepository.findAll();
    }
}

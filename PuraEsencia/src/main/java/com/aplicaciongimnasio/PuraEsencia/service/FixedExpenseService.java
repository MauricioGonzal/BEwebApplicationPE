package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.FixedExpenseRequest;
import com.aplicaciongimnasio.PuraEsencia.model.FixedExpense;
import com.aplicaciongimnasio.PuraEsencia.repository.FixedExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FixedExpenseService {
    @Autowired
    private FixedExpenseRepository fixedExpenseRepository;

    public FixedExpense createFixedExpense(FixedExpenseRequest request) {
        FixedExpense expense = new FixedExpense();
        expense.setName(request.getName());
        expense.setMonthlyAmount(request.getMonthlyAmount());
        expense.setStartDate(request.getStartDate());
        expense.setTotalInstallments(request.getTotalInstallments());
        expense.setRemainingInstallments(request.getTotalInstallments());
        expense.setIsActive(true);

        return fixedExpenseRepository.save(expense);
    }

    public List<FixedExpense> getAll() {
        return fixedExpenseRepository.findByIsActive(true);
    }

    public Optional<FixedExpense> update(Long id, FixedExpenseRequest fixedExpenseRequest){
        return fixedExpenseRepository.findById(id)
                .map(expense -> {
                    expense.setName(fixedExpenseRequest.getName());
                    expense.setMonthlyAmount(fixedExpenseRequest.getMonthlyAmount());
                    expense.setStartDate(fixedExpenseRequest.getStartDate());
                    expense.setTotalInstallments(fixedExpenseRequest.getTotalInstallments());
                    expense.setRemainingInstallments(fixedExpenseRequest.getTotalInstallments() - (expense.getTotalInstallments() - expense.getRemainingInstallments()));

                    return fixedExpenseRepository.save(expense);
                });
    }

    public Boolean logicDelete(Long id) {
        FixedExpense fixedExpense = fixedExpenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gasto no encontrado con ID: " + id));

        fixedExpense.setIsActive(false);
        fixedExpenseRepository.save(fixedExpense);
        return true;
    }


}

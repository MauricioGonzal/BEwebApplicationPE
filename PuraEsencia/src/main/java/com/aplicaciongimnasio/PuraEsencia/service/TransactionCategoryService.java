package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.TransactionCategoryRequest;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import com.aplicaciongimnasio.PuraEsencia.model.enums.Role;
import com.aplicaciongimnasio.PuraEsencia.repository.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionCategoryService {

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    public List<TransactionCategory> getAllTransactionCategory() {
        return transactionCategoryRepository.findAll();
    }

    public List<TransactionCategory> getAllTransactionCategoryForPayments() {
        return transactionCategoryRepository.findByRoleAcceptedIsNotNull();
    }

    public TransactionCategory createTransactionCategory(TransactionCategoryRequest transactionCategoryRequest){
        Role role = Role.valueOf(transactionCategoryRequest.getRoleAccepted().toUpperCase());
        if(transactionCategoryRepository.findByName(transactionCategoryRequest.getName()).isPresent()){
            throw new RuntimeException("Ya existe una categoria con ese nombre");
        }
        TransactionCategory transactionCategory = new TransactionCategory();
        transactionCategory.setName(transactionCategoryRequest.getName());
        transactionCategory.setRoleAccepted(role);
        return transactionCategoryRepository.save(transactionCategory);
    }
}

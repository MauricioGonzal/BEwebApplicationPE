package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
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

    public TransactionCategory createTransactionCategory(TransactionCategory transactionCategory){
        if(transactionCategoryRepository.findByName(transactionCategory.getName()).isPresent()){
            throw new RuntimeException("Ya existe una categoria con ese nombre");
        }
        return transactionCategoryRepository.save(transactionCategory);
    }
}

package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import com.aplicaciongimnasio.PuraEsencia.service.TransactionCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/transaction-category")
public class TransactionCategoryController {

    @Autowired
    private TransactionCategoryService transactionCategoryService;

    @GetMapping
    public List<TransactionCategory> getAllTransactionCategory() {
        return transactionCategoryService.getAllTransactionCategory();
    }

    @PostMapping
    public TransactionCategory createTransactionCategory(@RequestBody TransactionCategory transactionCategory) {
        return transactionCategoryService.createTransactionCategory(transactionCategory);
    }
}

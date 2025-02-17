package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceListService {

    @Autowired
    PriceListRepository priceListRepository;
    // Crear un nuevo ejercicio
    public PriceList createPrice(PriceList priceList) {
        return priceListRepository.save(priceList);
    }

    public List<PriceList> getAllPriceList() {
        return priceListRepository.findAll();
    }

    public Float getAmountForTransaction(TransactionCategory transactionCategory, PaymentMethod paymentMethod) {
        return priceListRepository.findByTransactionCategoryAndPaymentMethod(transactionCategory, paymentMethod)
                .map(PriceList::getAmount)
                .orElseThrow(() -> new RuntimeException("No price found for transactionCategory: " + transactionCategory.getName() + " and paymentMethod: " + paymentMethod.getName()));
    }

}

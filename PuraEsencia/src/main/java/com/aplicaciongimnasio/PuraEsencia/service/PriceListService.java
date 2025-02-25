package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
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

    public Float getAmountForTransaction(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership) {
        return priceListRepository.findByTransactionCategoryAndPaymentMethodAndMembership(transactionCategory, paymentMethod, membership)
                .map(PriceList::getAmount)
                .orElseThrow(() -> new RuntimeException("No price found for transactionCategory: " + transactionCategory.getName() + " and paymentMethod: " + paymentMethod.getName()));
    }

}

package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PriceListService {

    @Autowired
    PriceListRepository priceListRepository;
    // Crear un nuevo ejercicio
    public PriceList createPrice(PriceList priceList) {

        if(getActive(priceList.getTransactionCategory(), priceList.getPaymentMethod(), priceList.getMembership()).isPresent()){
            throw new RuntimeException("Price already exists for transactionCategory: " + priceList.getTransactionCategory().getName() + " and paymentMethod: " + priceList.getPaymentMethod().getName()+ " and membership: " + priceList.getMembership().getName());
        }

        return priceListRepository.save(priceList);
    }

    public List<PriceList> getAllPriceList() {
        return priceListRepository.findAll();
    }

    public Float getAmountForTransaction(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership) {
        return priceListRepository.findByTransactionCategoryAndPaymentMethodAndMembershipAndIsActive(transactionCategory, paymentMethod, membership, true)
                .map(PriceList::getAmount)
                .orElseThrow(() -> new RuntimeException("No price found for transactionCategory: " + transactionCategory.getName() + " and paymentMethod: " + paymentMethod.getName()));
    }

    public Optional<PriceList> getActive(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership) {
        return priceListRepository.findByTransactionCategoryAndPaymentMethodAndMembershipAndIsActive(transactionCategory, paymentMethod, membership, true);
    }

    public PriceList updateAmount(Long id, Float newAmount){
        // 1. Buscar el registro actual
        Optional<PriceList> existingPriceListOpt = priceListRepository.findById(id);
        if (existingPriceListOpt.isEmpty()) {
            throw new RuntimeException("No price found with id: " + id);
        }

        PriceList existingPriceList = existingPriceListOpt.get();

        // 2. Desactivar el registro actual
        existingPriceList.setIsActive(false);
        existingPriceList.setValidUntil(LocalDate.now());
        priceListRepository.save(existingPriceList);

        // 3. Crear un nuevo registro con el nuevo monto y activo
        PriceList newPriceList = new PriceList();
        newPriceList.setTransactionCategory(existingPriceList.getTransactionCategory());
        newPriceList.setProduct(existingPriceList.getProduct());
        newPriceList.setPaymentMethod(existingPriceList.getPaymentMethod());
        newPriceList.setMembership(existingPriceList.getMembership());
        newPriceList.setAmount(newAmount);
        newPriceList.setValidFrom(LocalDate.now());
        newPriceList.setIsActive(true);
        newPriceList.setValidUntil(null); // No tiene fecha de fin porque es el actual

        return priceListRepository.save(newPriceList);
    }

}

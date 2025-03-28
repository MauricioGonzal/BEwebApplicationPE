package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.MembershipResponse;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PriceListService {

    @Autowired
    PriceListRepository priceListRepository;

    public PriceList createPrice(PriceList priceList) {
        if(getActive(priceList.getTransactionCategory(), priceList.getPaymentMethod(), priceList.getMembership()).isPresent()){
            throw new RuntimeException("Ya existe un precio para " + priceList.getTransactionCategory().getName() + " ,  " + priceList.getPaymentMethod().getName()+ " y membresia: " + priceList.getMembership().getName());
        }

        return priceListRepository.save(priceList);
    }

    public List<PriceList> getAllPriceList() {
        return priceListRepository.findAll();
    }

    public List<PriceList> getAllForPayments() {
        return priceListRepository.findByMembershipIsNotNullAndIsActive(true);
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
        Optional<PriceList> existingPriceListOpt = priceListRepository.findById(id);
        if (existingPriceListOpt.isEmpty()) {
            throw new RuntimeException("No price found with id: " + id);
        }

        PriceList existingPriceList = existingPriceListOpt.get();

        existingPriceList.setIsActive(false);
        existingPriceList.setValidUntil(LocalDate.now());
        priceListRepository.save(existingPriceList);

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

    public Boolean updatePriceLists(Map<String, Float> priceListsToEdit){
            for (Map.Entry<String, Float> entry : priceListsToEdit.entrySet()) {
                Long priceListId;
                try {
                    priceListId = Long.parseLong(entry.getKey()); // Convertir String a Long
                } catch (NumberFormatException e) {
                    throw new RuntimeException("ID de PriceList inválido: " + entry.getKey(), e);
                }
                Float amount = entry.getValue();
                PriceList priceList = priceListRepository.findById(priceListId).orElseThrow(() -> new RuntimeException("Precio no encontrado"));
                priceList.setValidUntil(LocalDate.now());
                priceList.setIsActive(false);
                priceListRepository.save(priceList);

                PriceList newPriceList = new PriceList();
                newPriceList.setTransactionCategory(priceList.getTransactionCategory());
                newPriceList.setPaymentMethod(priceList.getPaymentMethod());
                newPriceList.setAmount(amount);
                newPriceList.setProduct(priceList.getProduct());
                priceListRepository.save(newPriceList);
            }
        return true;
    }

    public Boolean logicDelete(Long id) {
        PriceList priceList = priceListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado con ID: " + id));

        priceList.setIsActive(false);
        priceList.setValidUntil(LocalDate.now());
        priceListRepository.save(priceList);
        return true;
    }

}

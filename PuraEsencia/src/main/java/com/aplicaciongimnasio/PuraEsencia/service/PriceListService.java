package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.PriceListEditRequest;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PriceListService {

    @Autowired
    PriceListRepository priceListRepository;

    public PriceList createPrice(PriceList priceList) {
        /*if(getActive(priceList.getTransactionCategory(), priceList.getPaymentMethod(), priceList.getMembership()).isPresent()){
            throw new RuntimeException("Ya existe un precio para " + priceList.getTransactionCategory().getName() + " ,  " + priceList.getPaymentMethod().getName()+ " y membresia: " + priceList.getMembership().getName());
        }*/

        return priceListRepository.save(priceList);
    }

    public List<PriceList> getAllPriceList() {
        return priceListRepository.findAll();
    }

    public List<PriceList> getAllForPayments() {
        return priceListRepository.findByMembershipIsNotNullAndIsActive(true);
    }

    public Float getAmountForTransaction(Product product, PaymentMethod paymentMethod, Membership membership) {
        return priceListRepository.findByProductAndPaymentMethodAndMembershipAndIsActive(product, paymentMethod, membership, true)
                .map(PriceList::getAmount)
                .orElseThrow(() -> new RuntimeException("No price found for paymentMethod: " + paymentMethod.getName() + "and" + product == null ? product.getName() : membership.getName()));
    }

    public Optional<PriceList> getActive(Product product, PaymentMethod paymentMethod, Membership membership) {
        return priceListRepository.findByProductAndPaymentMethodAndMembershipAndIsActive(product, paymentMethod, membership, true);
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

    public Boolean updatePriceLists(List<PriceListEditRequest> priceListsToEdit){
        PriceList priceList = null;
        for(PriceListEditRequest priceListEditRequest : priceListsToEdit){
            if(priceListEditRequest.getId() != null){
                Float amount = priceListEditRequest.getAmount();

                Long priceListId = priceListEditRequest.getId();

                priceList = priceListRepository.findById(priceListId).orElseThrow(() -> new RuntimeException("Precio no encontrado"));
                priceList.setValidUntil(LocalDate.now());
                priceList.setIsActive(false);
                priceListRepository.save(priceList);
                if(amount == 0) continue;
                PriceList priceListEdited = new PriceList();
                priceListEdited.setTransactionCategory(priceList.getTransactionCategory());
                priceListEdited.setPaymentMethod(priceList.getPaymentMethod());
                priceListEdited.setAmount(amount);
                priceListEdited.setProduct(priceList.getProduct());
                priceListRepository.save(priceListEdited);
            }

        }

        if(priceList == null) throw new RuntimeException("Error. Contacta con soporte.");

        for(PriceListEditRequest priceListEditRequest : priceListsToEdit) {
            if(priceListEditRequest.getId() == null){
                PriceList newPriceList = new PriceList();
                newPriceList.setProduct(priceList.getProduct());
                newPriceList.setTransactionCategory(priceList.getTransactionCategory());
                newPriceList.setAmount(priceListEditRequest.getAmount());
                newPriceList.setPaymentMethod(priceListEditRequest.getPaymentMethod());
                priceListRepository.save(newPriceList);
            }

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

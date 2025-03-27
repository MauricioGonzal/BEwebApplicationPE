package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.MembershipRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.MembershipResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import com.aplicaciongimnasio.PuraEsencia.repository.MembershipRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.TransactionCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MembershipService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private PriceListService priceListService;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    public List<Membership> getAllMemberships() {
        return membershipRepository.findByIsActive(true);
    }

    public List<MembershipResponse> getAllMembershipsAndPriceLists() {
        return membershipRepository.findAllWithPiceList();
    }

    public Membership getById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada con ID: " + id));
    }

    public Boolean createMembershipAndPrice(MembershipRequest membershipRequest) {
        List<Object> existences = priceListRepository.getExistencesOfSameProduct(membershipRequest.getName(), membershipRequest.getPaymentMethod());
        if(!existences.isEmpty()){
            throw new RuntimeException("Ya existe un precio para un producto con el mismo nombre y medio de pago");
        }

        Membership membership = new Membership();
        membership.setName(membershipRequest.getName());
        membership.setMaxClasses(membershipRequest.getMaxClasses());
        membership.setMaxDays(membershipRequest.getMaxDays());
        membership.setTransactionCategory(membershipRequest.getTransactionCategory());
        membership = membershipRepository.save(membership);

        PriceList priceList = new PriceList();
        priceList.setMembership(membership);
        priceList.setAmount(membershipRequest.getAmount());
        priceList.setPaymentMethod(membershipRequest.getPaymentMethod());
        priceList.setTransactionCategory(transactionCategoryRepository.findByName("Producto")
                .orElseThrow(() -> new RuntimeException("Categoria de transaccion no encontrada")));
        priceList.setValidFrom(LocalDate.now());
        priceListRepository.save(priceList);

        return true;
    }

    public Boolean deleteMembershipWithPrice(MembershipResponse membershipResponse){
        priceListService.logicDelete(membershipResponse.getPriceList().getId());
        logicDelete(membershipResponse.getMembership().getId());
        return true;
    }

    public Boolean logicDelete(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        membership.setIsActive(false);
        membershipRepository.save(membership);
        return true;
    }

    @Transactional
    public MembershipResponse update(Long id, Map<String, Object> membershipRequest){
        var newData = (LinkedHashMap<String, ?>)membershipRequest.get("data");
        var membershipResponse =(LinkedHashMap<String, ?>) membershipRequest.get("membershipResponse");

        Membership membership = membershipRepository.findById(id).orElseThrow(() -> new RuntimeException("La membresia no existe"));

        LinkedHashMap<String, Object> paymentMethodData = (LinkedHashMap<String, Object>) newData.get("paymentMethod");
        LinkedHashMap<String, Object> transactionCategoryData = (LinkedHashMap<String, Object>) newData.get("transactionCategory");

        PaymentMethod paymentMethod = objectMapper.convertValue(paymentMethodData, PaymentMethod.class);
        TransactionCategory transactionCategory = objectMapper.convertValue(transactionCategoryData, TransactionCategory.class);


        if(!membershipRepository.findMembership(paymentMethod, transactionCategory, id).isEmpty())
            throw new RuntimeException("Ya existe una membresia con esas características");

        LinkedHashMap<String, Object> membershipData = (LinkedHashMap<String, Object>) membershipResponse.get("membership");
        LinkedHashMap<String, Object> priceListData = (LinkedHashMap<String, Object>) membershipResponse.get("priceList");


        Membership membershipToDeactivate = objectMapper.convertValue(membershipData, Membership.class);

        PriceList priceListToDeactivate = objectMapper.convertValue(priceListData, PriceList.class);

        membershipToDeactivate.setIsActive(false);
        membershipRepository.save(membershipToDeactivate);

        Membership membershipToSave = new Membership();
        membershipToSave.setName((String)newData.get("name"));
        membershipToSave.setTransactionCategory(transactionCategory);
        membershipToSave.setMaxDays((Integer)newData.get("maxDays"));
        membershipToSave.setMaxClasses((Integer) newData.get("maxClasses"));
        Membership newMembership = membershipRepository.save(membershipToSave);

        priceListToDeactivate.setIsActive(false);
        priceListToDeactivate.setValidUntil(LocalDate.now());
        priceListRepository.save(priceListToDeactivate);

        PriceList newPriceList = new PriceList();
        newPriceList.setTransactionCategory(transactionCategory);
        newPriceList.setPaymentMethod(paymentMethod);
        newPriceList.setMembership(newMembership);
        newPriceList.setAmount(Float.parseFloat(newData.get("amount").toString()));
        newPriceList.setValidFrom(LocalDate.now());
        newPriceList.setIsActive(true);
        newPriceList.setValidUntil(null); // No tiene fecha de fin porque es el actual
        newPriceList = priceListRepository.save(newPriceList);

        return new MembershipResponse(newMembership, newPriceList);
    }

}

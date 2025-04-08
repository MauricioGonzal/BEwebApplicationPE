package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.MembershipRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.MembershipResponse;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private MembershipItemRepository membershipItemRepository;

    public List<Membership> getAllMemberships() {
        return membershipRepository.findByIsActive(true);
    }

    public List<MembershipResponse> getAllMembershipsAndPriceLists() {
        List<PriceList> priceLists = priceListRepository.findActivePriceListsWithMembership();

        // Crear un mapa que asocie cada Membership con su lista de PriceLists
        Map<Membership, List<PriceList>> membershipPriceListMap = new HashMap<>();

        // Agrupar las PriceLists por Membership
        for (PriceList priceList : priceLists) {
            Membership membership = priceList.getMembership();  // Obtener la Membership asociada

            // Si la Membership ya está en el mapa, agregamos el PriceList, sino, la creamos
            membershipPriceListMap
                    .computeIfAbsent(membership, k -> new ArrayList<>())
                    .add(priceList);
        }

        // Crear los MembershipResponse con la lista de PriceLists
        List<MembershipResponse> responses = new ArrayList<>();
        for (Map.Entry<Membership, List<PriceList>> entry : membershipPriceListMap.entrySet()) {
            Membership membership = entry.getKey();
            List<PriceList> activePriceLists = entry.getValue();
            List<Membership> membershipAssociated = membershipRepository.getAssociatedMemberships(membership);
            responses.add(new MembershipResponse(membership, membershipAssociated, activePriceLists));
        }

        return responses;

    }

    public List<MembershipResponse> getAllSimpleMembershipsAndPriceLists() {
        List<PriceList> priceLists = priceListRepository.findActivePriceListsWithSimpleMembership();

        // Crear un mapa que asocie cada Membership con su lista de PriceLists
        Map<Membership, List<PriceList>> membershipPriceListMap = new HashMap<>();

        // Agrupar las PriceLists por Membership
        for (PriceList priceList : priceLists) {
            Membership membership = priceList.getMembership();  // Obtener la Membership asociada

            // Si la Membership ya está en el mapa, agregamos el PriceList, sino, la creamos
            membershipPriceListMap
                    .computeIfAbsent(membership, k -> new ArrayList<>())
                    .add(priceList);
        }

        // Crear los MembershipResponse con la lista de PriceLists
        List<MembershipResponse> responses = new ArrayList<>();
        for (Map.Entry<Membership, List<PriceList>> entry : membershipPriceListMap.entrySet()) {
            Membership membership = entry.getKey();
            List<PriceList> activePriceLists = entry.getValue();
            List<Membership> membershipAssociated = membershipRepository.getAssociatedMemberships(membership);
            responses.add(new MembershipResponse(membership, membershipAssociated, activePriceLists));
        }

        return responses;

    }

    public Membership getById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membresia no encontrada con ID: " + id));
    }

    @Transactional
    public Boolean createMembershipAndPrice(MembershipRequest membershipRequest) {
        Membership membership = new Membership();
        membership.setName(membershipRequest.getName());
        membership.setMaxClasses(membershipRequest.getMaxClasses());
        membership.setMaxDays(membershipRequest.getMaxDays());
        //membership.setTransactionCategory(membershipRequest.getTransactionCategory());
        membership.setMembershipType(membership.getMembershipType());
        membership.setArea(membershipRequest.getArea());
        membership.setMembershipType(membershipRequest.getMembershipType());

        membership = membershipRepository.save(membership);

        var combinedMembershipIds = membershipRequest.getCombinedMembershipIds();
        if(membershipRequest.getCombinedMembershipIds() != null && !membershipRequest.getCombinedMembershipIds().isEmpty()){
            for(Long membershipIdToAssociate : combinedMembershipIds){
                Membership membershipToAssociate = membershipRepository.findById(membershipIdToAssociate).orElseThrow(() -> new RuntimeException("No se encuentra la membresia que se quiere combinar"));
                MembershipItem membershipItem = new MembershipItem();
                membershipItem.setMembershipPrincipal(membership);
                membershipItem.setMembershipAssociated(membershipToAssociate);
                membershipItemRepository.save(membershipItem);
            }
        }

        for (Map.Entry<Long, Float> entry : membershipRequest.getPrices().entrySet()) {
            if(entry.getValue() == null) continue;
            PaymentMethod pm = paymentMethodRepository.findById(entry.getKey()).orElseThrow(() -> new RuntimeException("Payment Method not found"));
            List<Object> existences = priceListRepository.getExistencesOfSameProduct(membershipRequest.getName(), pm);

            if(!existences.isEmpty()){
                throw new RuntimeException("Ya existe un precio para un producto con el mismo nombre y medio de pago");
            }

            PriceList priceList = new PriceList();
            priceList.setMembership(membership);
            priceList.setAmount(entry.getValue());
            priceList.setPaymentMethod(pm);
            //priceList.setTransactionCategory(membershipRequest.getTransactionCategory());
            priceList.setValidFrom(LocalDate.now());
            priceListRepository.save(priceList);
        }

        return true;
    }

    @Transactional
    public Boolean deleteMembershipWithPrice(MembershipResponse membershipResponse){
        for (PriceList priceList : membershipResponse.getPriceLists()) {
            priceListService.logicDelete(priceList.getId());
        }
        logicDelete(membershipResponse.getMembership().getId());
        return true;
    }

    public Boolean logicDelete(Long id) {
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        membership.setIsActive(false);
        membershipRepository.save(membership);

        if(Objects.equals(membership.getMembershipType().getName(), "Simple")){
            //Buscamos si en alguna membresia combinada existe esta membresia
            List<MembershipItem> associated = membershipItemRepository.findByMembershipAssociatedAndIsActive(membership, true);
            if(!associated.isEmpty())  throw new RuntimeException("Esta membresia esta asociada a una membresia combinada. No se puede eliminar.");
        }
        List<MembershipItem> membershipList = membershipItemRepository.findByMembershipPrincipalAndIsActive(membership, true);

        for(MembershipItem membershipItem : membershipList){
            membershipItem.setIsActive(false);
            membershipItemRepository.save(membershipItem);
        }
        return true;
    }

    @Transactional
    public MembershipResponse update(Long id, @RequestBody MembershipRequest membershipRequest) {
        // Obtener la membresía existente
        Membership existingMembership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La membresía no existe"));

        if(Objects.equals(existingMembership.getMembershipType().getName(), "Simple") && !membershipItemRepository.findByMembershipAssociatedAndIsActive(existingMembership, true).isEmpty())
            throw new RuntimeException("Esta membresia no se puede editar. Es parte de una membresia combinada activa.");

        List<MembershipItem> existingMembershipItems = membershipItemRepository.findByMembershipPrincipalAndIsActive(existingMembership, true);
        existingMembershipItems.forEach(mi -> {
            mi.setIsActive(false);
            membershipItemRepository.save(mi);
        });

        TransactionCategory transactionCategory = membershipRequest.getTransactionCategory();
        MembershipType membershipType = membershipRequest.getMembershipType();

        // Crear la nueva membresía
        Membership membershipToSave = new Membership();
        membershipToSave.setName(membershipRequest.getName());
        membershipToSave.setTransactionCategory(transactionCategory);
        membershipToSave.setMembershipType(membershipType);
        membershipToSave.setMaxDays(membershipRequest.getMaxDays());
        membershipToSave.setMaxClasses(membershipRequest.getMaxClasses());
        membershipToSave.setIsActive(true);
        Membership newMembership = membershipRepository.save(membershipToSave);

        // Si es combinada, se agregan las membresías combinadas
        if ("Combinada".equalsIgnoreCase(membershipType.getName())) {
            List<Long> combinedIds = membershipRequest.getCombinedMembershipIds();
            if (combinedIds != null && !combinedIds.isEmpty()) {
                List<Membership> combinedMemberships = membershipRepository.findAllById(combinedIds);
                for(Membership membership : combinedMemberships){
                    MembershipItem membershipItem = new MembershipItem();
                    membershipItem.setMembershipPrincipal(newMembership);
                    membershipItem.setMembershipAssociated(membership);
                    membershipItemRepository.save(membershipItem);
                }
            }
        }


        // Desactivar la membresía anterior
        existingMembership.setIsActive(false);
        membershipRepository.save(existingMembership);

        // Crear los nuevos precios
        Map<Long, Float> prices = membershipRequest.getPrices();
        List<PriceList> newPriceLists = new ArrayList<>();

        for (Map.Entry<Long, Float> entry : prices.entrySet()) {
            Float amount = entry.getValue();
            if (amount == null) continue;

            Long paymentMethodId = entry.getKey();

            PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new RuntimeException("El método de pago no existe"));

            // Verificar duplicado
            List<Membership> existing = membershipRepository.findMembership(
                    paymentMethod,
                    transactionCategory,
                    id,
                    membershipRequest.getMaxDays(),
                    membershipRequest.getMaxClasses()
            );

            if (!existing.isEmpty()) {
                throw new RuntimeException("Ya existe una membresía con esas características");
            }

            PriceList priceList = new PriceList();
            priceList.setTransactionCategory(transactionCategory);
            priceList.setPaymentMethod(paymentMethod);
            priceList.setMembership(newMembership);
            priceList.setAmount(amount);
            priceList.setValidFrom(LocalDate.now());
            priceList.setIsActive(true);
            priceList.setValidUntil(null);

            newPriceLists.add(priceListRepository.save(priceList));
        }

        // Desactivar precios anteriores
        List<PriceList> oldPriceLists = priceListRepository.findByMembership(existingMembership);
        for (PriceList pl : oldPriceLists) {
            pl.setIsActive(false);
            pl.setValidUntil(LocalDate.now());
            priceListRepository.save(pl);
        }

        return new MembershipResponse(newMembership, List.of(), newPriceLists);
    }



}

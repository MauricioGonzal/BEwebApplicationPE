package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.MembershipRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.MembershipResponse;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.MembershipRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentMethodRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.TransactionCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            responses.add(new MembershipResponse(membership, activePriceLists));
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
        membership.setTransactionCategory(membershipRequest.getTransactionCategory());
        membership = membershipRepository.save(membership);

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
            priceList.setTransactionCategory(membershipRequest.getTransactionCategory());
            priceList.setValidFrom(LocalDate.now());
            priceListRepository.save(priceList);
        }

        return true;
    }

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
        return true;
    }

    @Transactional
    public MembershipResponse update(Long id, Map<String, Object> membershipRequest) {
        // Obtener la membresía existente
        Membership membership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La membresía no existe"));
        // Obtener los datos de la categoría de transacción
        LinkedHashMap<String, Object> transactionCategoryData = (LinkedHashMap<String, Object>) membershipRequest.get("transactionCategory");
        TransactionCategory transactionCategory = objectMapper.convertValue(transactionCategoryData, TransactionCategory.class);

        // Crear la nueva membresía
        Membership membershipToSave = new Membership();
        membershipToSave.setName((String) membershipRequest.get("name"));
        membershipToSave.setTransactionCategory(transactionCategory);
        membershipToSave.setMaxDays((Integer) membershipRequest.get("maxDays"));
        membershipToSave.setMaxClasses((Integer) membershipRequest.get("maxClasses"));
        Membership newMembership = membershipRepository.save(membershipToSave);

        // Desactivar la membresía actual
        membership.setIsActive(false);
        membershipRepository.save(membership);

        // Obtener los precios de la solicitud
        Map<String, Object> prices = (Map<String, Object>) membershipRequest.get("prices");

        List<PriceList> newPriceLists = new ArrayList<>();
        for (Map.Entry<String, Object> entry : prices.entrySet()) {
            // Verificar si ya existe una membresía con las mismas características
            if(entry.getValue() == "" || entry.getValue() == null) continue;
            Float amount = Float.parseFloat(entry.getValue().toString());  // Monto


            Long paymentMethodId = Long.parseLong(entry.getKey());  // ID de paymentMethod

            // Obtener el PaymentMethod correspondiente
            PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new RuntimeException("El método de pago no existe"));
            List<Membership> existingMembership = membershipRepository.findMembership(paymentMethod, transactionCategory, id, (Integer) membershipRequest.get("maxDays"), ((Integer) membershipRequest.get("maxClasses")));
            if (!existingMembership.isEmpty()) {
                throw new RuntimeException("Ya existe una membresía con esas características");
            }
            // Crear un nuevo PriceList por cada paymentMethod y amount
            PriceList newPriceList = new PriceList();
            newPriceList.setTransactionCategory(transactionCategory);
            newPriceList.setPaymentMethod(paymentMethod);
            newPriceList.setMembership(newMembership);
            newPriceList.setAmount(amount);
            newPriceList.setValidFrom(LocalDate.now());
            newPriceList.setIsActive(true);
            newPriceList.setValidUntil(null);  // No tiene fecha de fin porque es el precio actual

            newPriceList = priceListRepository.save(newPriceList);
            newPriceLists.add(newPriceList);
        }

        List<PriceList> priceListsToDeactivate = priceListRepository.findByMembership(membership);
        for(PriceList pl : priceListsToDeactivate){
            pl.setIsActive(false);
            pl.setValidUntil(LocalDate.now());
            priceListRepository.save(pl);
        }

        // Retornar la respuesta con la nueva membresía y sus precios
        return new MembershipResponse(newMembership, newPriceLists);
    }


}

package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.AreaRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.MembershipItemRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MembershipItemRepository membershipItemRepository;

    public List<Area> getAll() {
        return areaRepository.findAll();
    }

    public Area create (Area area){
        if(areaRepository.findByName(area.getName()) != null ) throw new RuntimeException("Ya existe un area con ese nombre");

        return areaRepository.save(area);
    }

    public List<Area> getAreaByUser(Long userId) {
        List<Payment> activePayments = paymentRepository.findLatestActivePaymentsByUser(LocalDate.now(), userId);
        if (!activePayments.isEmpty()) {
            List<Area> areas = new ArrayList<>();
            for (Payment payment : activePayments) {
                Membership membership = payment.getMembership();
                if (Objects.equals(membership.getMembershipType().getName(), "Combinada")) {
                    List<MembershipItem> membershipItemList = membershipItemRepository.findByMembershipPrincipal(membership);
                    for (MembershipItem mi : membershipItemList) {
                        Area area = mi.getMembershipAssociated().getArea();
                        if (!areas.contains(area)) {
                            areas.add(area);
                        }
                    }
                } else {
                    Area area = membership.getArea();
                    if (!areas.contains(area)) {
                        areas.add(area);
                    }
                }
            }
            return areas;
        } else {
            throw new RuntimeException("ERROR");
        }
    }

}

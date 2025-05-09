package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.MembershipItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipItemRepository extends JpaRepository<MembershipItem, Long> {
    List<MembershipItem> findByMembershipPrincipalAndIsActive(Membership membership, Boolean isActive);
    List<MembershipItem> findByMembershipPrincipal(Membership membership);
    List<MembershipItem> findByMembershipAssociatedAndIsActive(Membership membership, Boolean isActive);
    List<MembershipItem> findByMembershipAssociated(Membership membership);

}

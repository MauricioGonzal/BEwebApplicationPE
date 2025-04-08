package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByName(String name);
    Optional<Membership> findByNameAndIdNot(String name, Long id);
    List<Membership> findByIsActive(Boolean isActive);

    @Query("""
        SELECT m FROM Membership m 
        WHERE m.transactionCategory = :transactionCategory 
        AND m.maxClasses >= :minClasses
        ORDER BY m.maxClasses ASC
        LIMIT 1
    """)
    Optional<Membership> findClosestMembership(
            @Param("transactionCategory") TransactionCategory transactionCategory,
            @Param("minClasses") Integer minClasses
    );

    @Query("SELECT m FROM Membership m JOIN PriceList pl ON pl.membership = m " +
            "WHERE pl.isActive = true " +
            "AND pl.paymentMethod = :paymentMethod " +
            "AND m.transactionCategory = :transactionCategory " +
            "AND m.id != :id " +
            "AND m.maxDays = :maxDays " +
            "AND m.maxClasses = :maxClasses")
    List<Membership> findMembership(@Param("paymentMethod") PaymentMethod paymentMethod,
                                    @Param("transactionCategory") TransactionCategory transactionCategory,
                                    @Param("id") Long id,
                                    @Param("maxDays") Integer maxDays,
                                    @Param("maxClasses") Integer maxClasses);

    @Query("SELECT m FROM Membership m JOIN MembershipItem mi ON mi.membershipAssociated = m WHERE mi.membershipPrincipal = :principalMembership")
    List<Membership> getAssociatedMemberships(@Param("principalMembership") Membership principalMembership);

}

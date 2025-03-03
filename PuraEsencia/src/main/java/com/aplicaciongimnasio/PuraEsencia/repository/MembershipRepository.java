package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Optional<Membership> findByName(String name);

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

}

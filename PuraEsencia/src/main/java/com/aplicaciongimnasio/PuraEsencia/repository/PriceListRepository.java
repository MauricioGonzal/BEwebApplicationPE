package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    Optional<PriceList> findByProductAndPaymentMethodAndMembershipAndIsActive(Product product, PaymentMethod paymentMethod, Membership membership, Boolean isActive);
    List<PriceList> findByMembershipIsNotNullAndIsActive(Boolean isActive);

    @Query("select pl.amount from Product p JOIN ProductStock ps ON ps.product = p JOIN PriceList pl ON pl.product = p WHERE p.name = :name AND pl.paymentMethod = :paymentMethod AND pl.isActive = TRUE")
    List<Object> getExistencesOfSameProduct(@Param("name") String name, @Param("paymentMethod") PaymentMethod paymentMethod);

    @Query("SELECT pl FROM PriceList pl JOIN FETCH pl.membership m WHERE pl.isActive = true")
    List<PriceList> findActivePriceListsWithMembership();

    @Query("SELECT pl FROM PriceList pl JOIN FETCH pl.membership m WHERE pl.isActive = true AND m.membershipType.id = 1")
    List<PriceList> findActivePriceListsWithSimpleMembership();

    @Query("SELECT pl FROM PriceList pl LEFT JOIN FETCH pl.product p JOIN FETCH ProductStock ps ON ps.product = p WHERE pl.isActive = true")
    List<PriceList> findActivePriceListsWithProductAndStock();

    List<PriceList> findByMembership(Membership membership);

    List<PriceList> findByProduct(Product product);

}

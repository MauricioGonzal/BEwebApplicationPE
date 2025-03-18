package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    Optional<PriceList> findByTransactionCategoryAndPaymentMethodAndMembershipAndIsActive(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership, Boolean isActive);
    List<PriceList> findByMembershipIsNotNullAndIsActive(Boolean isActive);

    @Query("select pl.amount from Product p JOIN ProductStock ps ON ps.product = p JOIN PriceList pl ON pl.product = p WHERE p.name = :name AND pl.paymentMethod = :paymentMethod AND pl.isActive = TRUE")
    List<Object> getExistencesOfSameProduct(@Param("name") String name, @Param("paymentMethod") PaymentMethod paymentMethod);
}

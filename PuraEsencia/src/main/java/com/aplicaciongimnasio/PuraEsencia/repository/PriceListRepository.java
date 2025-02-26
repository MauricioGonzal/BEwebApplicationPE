package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, Long> {
    Optional<PriceList> findByTransactionCategoryAndPaymentMethodAndMembershipAndIsActive(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership, Boolean isActive);
    //Optional<PriceList> findByTransactionCategoryAndPaymentMethodAndMembershipAndIsActive(TransactionCategory transactionCategory, PaymentMethod paymentMethod, Membership membership, Boolean isActive);

}

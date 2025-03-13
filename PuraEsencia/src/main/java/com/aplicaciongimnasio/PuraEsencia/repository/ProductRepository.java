package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.dto.ProductResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new com.aplicaciongimnasio.PuraEsencia.dto.ProductResponse(p, pl, ps) FROM Product p JOIN PriceList pl ON pl.product.id = p.id JOIN ProductStock ps ON ps.product.id = p.id WHERE pl.isActive = true")
    List<ProductResponse> getAllPriceAndStock();
}

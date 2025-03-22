package com.aplicaciongimnasio.PuraEsencia.repository;

import com.aplicaciongimnasio.PuraEsencia.model.Product;
import com.aplicaciongimnasio.PuraEsencia.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    ProductStock findByProduct(Product product);
    List<ProductStock> findByIsActive(Boolean isActive);
    ProductStock findByIsActiveAndProduct(Boolean isActive, Product product);

}

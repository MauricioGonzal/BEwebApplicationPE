package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.model.ProductStock;
import com.aplicaciongimnasio.PuraEsencia.repository.ProductRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.ProductStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductStockService {

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private ProductRepository productRepository;

    public ProductStock createProductStock(ProductStock productStock) {
        var product = productRepository.findById(productStock.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if(productStockRepository.findByProduct(productStock.getProduct()) != null){
            throw new RuntimeException("Ya existe un stock para este producto");
        }
        return productStockRepository.save(productStock);
    }

    public ProductStock updateProductStock(Long id, Integer stock) {
        ProductStock productStock = productStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto Stock no encontrado"));
        productStock.setStock(stock);

        return productStockRepository.save(productStock);
    }

    public List<ProductStock> getAll() {
        return productStockRepository.findByIsActive(true);
    }

    public Boolean logicDelete(Long id) {
        ProductStock productStock = productStockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado con ID: " + id));

        productStock.setIsActive(false);
        productStockRepository.save(productStock);
        return true;
    }


}

package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.ProductRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.ProductResponse;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.Product;
import com.aplicaciongimnasio.PuraEsencia.model.ProductStock;
import com.aplicaciongimnasio.PuraEsencia.repository.PriceListRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.ProductRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.ProductStockRepository;
import com.aplicaciongimnasio.PuraEsencia.repository.TransactionCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<ProductResponse> getAllPriceAndStock() {
        return productRepository.getAllPriceAndStock();
    }

    public Boolean createProductStockPrice(ProductRequest productRequest) {
        List<Object> existences = priceListRepository.getExistencesOfSameProduct(productRequest.getName(), productRequest.getPaymentMethod());
        if(!existences.isEmpty()){
            throw new RuntimeException("Ya existe un precio para un producto con el mismo nombre y medio de pago");
        }

        Product product = new Product();
        product.setName(productRequest.getName());
        Product productSaved = productRepository.save(product);

        ProductStock productStock = new ProductStock();
        productStock.setStock(productRequest.getStock());
        productStock.setProduct(productSaved);

        productStockRepository.save(productStock);

        PriceList priceList = new PriceList();
        priceList.setProduct(productSaved);
        priceList.setAmount(productRequest.getAmount());
        priceList.setPaymentMethod(productRequest.getPaymentMethod());
        priceList.setTransactionCategory(transactionCategoryRepository.findByName("Producto")
                .orElseThrow(() -> new RuntimeException("Tipo de asistencia no encontrado")));
        priceList.setValidFrom(LocalDate.now());
        priceListRepository.save(priceList);

        return true;
    }
}

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

@Service
public class ProductService {

    @Autowired
    private ProductStockService productStockService;

    @Autowired
    private PriceListService priceListService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByIsActive(true);
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

    public Boolean deleteProductWithStockAndPrice(ProductResponse productResponse){
        priceListService.logicDelete(productResponse.getPriceList().getId());
        productStockService.logicDelete(productResponse.getProductStock().getId());
        logicDelete(productResponse.getProduct().getId());
        return true;
    }

    public Boolean logicDelete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        product.setIsActive(false);
        productRepository.save(product);
        return true;
    }
}

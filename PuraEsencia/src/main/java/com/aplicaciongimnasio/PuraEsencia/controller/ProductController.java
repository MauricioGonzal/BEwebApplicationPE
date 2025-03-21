package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.dto.ProductRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.ProductResponse;
import com.aplicaciongimnasio.PuraEsencia.model.Product;
import com.aplicaciongimnasio.PuraEsencia.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @GetMapping("/price-and-stock")
    public ResponseEntity<List<ProductResponse>> getAllPriceAndStock(){
        return ResponseEntity.ok(productService.getAllPriceAndStock());
    }

    @PostMapping("/create-product-stock-price")
    public ResponseEntity<?> createProductStockPrice(@RequestBody ProductRequest productRequest){
        Boolean result = productService.createProductStockPrice(productRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete-product-stock-price")
    public Boolean logicDelete(@RequestBody ProductResponse productResponse){
        return productService.deleteProductWithStockAndPrice(productResponse);
    }

}

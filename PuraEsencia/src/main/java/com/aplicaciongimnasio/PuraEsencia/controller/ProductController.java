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
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/price-and-stock")
    public ResponseEntity<List<ProductResponse>> getAllPriceAndStock(){
        return ResponseEntity.ok(productService.getAllProductsAndPriceLists());
    }

    @PostMapping("/create-product-stock-price")
    public ResponseEntity<?> createProductStockPrice(@RequestBody ProductRequest productRequest){
        Boolean result = productService.createProductStockPrice(productRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/delete-product-stock-price")
    public ResponseEntity<Boolean> logicDelete(@RequestBody ProductResponse productResponse){
        return ResponseEntity.ok(productService.deleteProductWithStockAndPrice(productResponse));
    }

}

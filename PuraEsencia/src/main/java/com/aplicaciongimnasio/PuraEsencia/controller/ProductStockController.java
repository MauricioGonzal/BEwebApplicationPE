package com.aplicaciongimnasio.PuraEsencia.controller;

import com.aplicaciongimnasio.PuraEsencia.model.ProductStock;
import com.aplicaciongimnasio.PuraEsencia.service.ProductStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")  // Permite solicitudes desde el frontend en localhost:3000
@RequestMapping("/api/products-stock")
public class ProductStockController {

    @Autowired
    private ProductStockService productStockService;

    @PostMapping("/create")
    public ResponseEntity<?> createProductStock(@RequestBody ProductStock productStock) {
        return ResponseEntity.ok(productStockService.createProductStock(productStock));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductStock> updateStock(@PathVariable Long id, @RequestBody Integer stock) {
        ProductStock productStock = productStockService.updateProductStock(id, stock);
        return ResponseEntity.ok(productStock);
    }

    @GetMapping
    public List<ProductStock> getAll() {
        return productStockService.getAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id) {
        boolean isRemoved = productStockService.logicDelete(id);
        if (isRemoved) {
            return ResponseEntity.ok("Producto eliminado con éxito");
        } else {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }
    }
}

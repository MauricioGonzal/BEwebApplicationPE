package com.aplicaciongimnasio.PuraEsencia.service;

import com.aplicaciongimnasio.PuraEsencia.dto.ProductRequest;
import com.aplicaciongimnasio.PuraEsencia.dto.ProductResponse;
import com.aplicaciongimnasio.PuraEsencia.model.*;
import com.aplicaciongimnasio.PuraEsencia.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public List<Product> getAllProducts() {
        return productRepository.findByIsActive(true);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<ProductResponse> getAllProductsAndPriceLists() {
        List<PriceList> priceLists = priceListRepository.findActivePriceListsWithProductAndStock();

        // Crear un mapa que asocie cada Membership con su lista de PriceLists
        Map<Product, List<PriceList>> productPriceListMap = new HashMap<>();

        // Agrupar las PriceLists por Membership
        for (PriceList priceList : priceLists) {
            Product product = priceList.getProduct();  // Obtener la Membership asociada

            // Si la Membership ya está en el mapa, agregamos el PriceList, sino, la creamos
            productPriceListMap
                    .computeIfAbsent(product, k -> new ArrayList<>())
                    .add(priceList);
        }

        // Crear los MembershipResponse con la lista de PriceLists
        List<ProductResponse> responses = new ArrayList<>();
        for (Map.Entry<Product, List<PriceList>> entry : productPriceListMap.entrySet()) {
            Product product = entry.getKey();
            List<PriceList> activePriceLists = entry.getValue();
            ProductStock productStock = productStockRepository.findByProduct(product);
            responses.add(new ProductResponse(product, activePriceLists, productStock ));
        }

        return responses;

    }

    public Boolean createProductStockPrice(ProductRequest productRequest) {
        // Crear el producto
        Product product = new Product();
        product.setName(productRequest.getName());
        Product productSaved = productRepository.save(product);

        // Crear el stock del producto
        ProductStock productStock = new ProductStock();
        productStock.setStock(productRequest.getStock());
        productStock.setProduct(productSaved);
        productStockRepository.save(productStock);

        // Recorrer todos los precios en el mapa 'prices'
        for (Map.Entry<Long, Float> entry : productRequest.getPrices().entrySet()) {
            if(entry.getValue() == null) continue;
            Long paymentMethodId = entry.getKey();
            Float amount = entry.getValue();

            PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                    .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

            // Verificar si ya existe una entrada para el mismo producto y medio de pago
            List<Object> existences = priceListRepository.getExistencesOfSameProduct(productRequest.getName(), paymentMethod);
            if (!existences.isEmpty()) {
                throw new RuntimeException("Ya existe un precio para un producto con el mismo nombre y medio de pago");
            }

            // Crear y guardar el PriceList para cada medio de pago
            PriceList priceList = new PriceList();
            priceList.setProduct(productSaved);
            priceList.setAmount(amount);
            priceList.setPaymentMethod(paymentMethod);
            priceList.setTransactionCategory(transactionCategoryRepository.findByName("Producto")
                    .orElseThrow(() -> new RuntimeException("Tipo de asistencia no encontrado")));
            priceList.setValidFrom(LocalDate.now());
            priceListRepository.save(priceList);

        }
        return true;
    }


    public Boolean deleteProductWithStockAndPrice(ProductResponse productResponse){
        for(PriceList priceList: productResponse.getPriceList()){
            priceListService.logicDelete(priceList.getId());
        }
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

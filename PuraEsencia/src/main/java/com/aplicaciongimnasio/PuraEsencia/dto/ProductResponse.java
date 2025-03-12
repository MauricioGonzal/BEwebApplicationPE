package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import com.aplicaciongimnasio.PuraEsencia.model.Product;
import com.aplicaciongimnasio.PuraEsencia.model.ProductStock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Product product;
    private PriceList priceList;
    private ProductStock productStock;
}

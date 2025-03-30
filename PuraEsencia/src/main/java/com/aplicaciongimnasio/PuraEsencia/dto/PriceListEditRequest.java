package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceListEditRequest {
    private Long id;
    private PaymentMethod paymentMethod;
    private Float amount;
}

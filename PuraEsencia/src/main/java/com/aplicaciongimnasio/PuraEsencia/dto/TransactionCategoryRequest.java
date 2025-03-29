package com.aplicaciongimnasio.PuraEsencia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionCategoryRequest {
    private String name;
    private String roleAccepted;
}

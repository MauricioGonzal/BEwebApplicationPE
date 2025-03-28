package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.TransactionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequest {
    private String name;
    private Integer maxClasses;
    private Integer maxDays;
    private TransactionCategory transactionCategory;
    private Map<Long, Float> prices;
}

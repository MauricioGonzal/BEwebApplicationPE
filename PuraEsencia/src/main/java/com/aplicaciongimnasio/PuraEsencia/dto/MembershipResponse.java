package com.aplicaciongimnasio.PuraEsencia.dto;

import com.aplicaciongimnasio.PuraEsencia.model.Exercise;
import com.aplicaciongimnasio.PuraEsencia.model.Membership;
import com.aplicaciongimnasio.PuraEsencia.model.PriceList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Membership membership;
    private List<PriceList> priceLists;
}

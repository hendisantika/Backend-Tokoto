package com.apiecommerce.tokoto.product.supply;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyRequest {

    private Integer quantity;

    private String supplyFrom;

    private SupplyType type;
}
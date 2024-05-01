package com.apiecommerce.tokoto.product.supply;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyResponse {

    private String id;

    private String product;

    private Integer quantity;

    private LocalDateTime supplyDate;

    private String supply_type;

    private String supplyFrom;
}

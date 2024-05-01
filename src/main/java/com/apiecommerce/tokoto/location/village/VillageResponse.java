package com.apiecommerce.tokoto.location.village;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VillageResponse {
    private String id;
    private String district;
    private String name;
}

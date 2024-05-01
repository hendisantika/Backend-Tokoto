package com.apiecommerce.tokoto.location.district;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistrictResponse {

    private String id;

    private String regency;

    private String name;
}

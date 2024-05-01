package com.apiecommerce.tokoto.product;

import com.apiecommerce.tokoto.categories.CategoriesRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    private List<Unit> units;
    private String title;
    private BigDecimal rating;
    private BigDecimal price;
    private Integer qty;
    private Integer sold;
    private Integer discount;
    private String description;
    private String details;
    private List<CategoriesRequest> categories;
}
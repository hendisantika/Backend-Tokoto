package com.apiecommerce.tokoto.product;

import com.apiecommerce.tokoto.categories.Categories;
import com.apiecommerce.tokoto.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private List<String> units;
    private String id;
    private String title;
    private BigDecimal rating;
    private BigDecimal price;
    private Integer qty;
    private Integer sold;
    private Integer discount;
    private String description;
    private String details;
    private List<String> categories;
    private List<String> imageName;
    private String uploadedBy;

    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setTitle(this.title);
        product.setRating(this.rating);
        product.setPrice(this.price);
        product.setQty(this.qty);
        product.setSold(this.sold);
        product.setDescription(this.description);
        product.setDetails(this.details);
        List<Categories> categoriesList = this.categories.stream().map(categoryName -> {
            Categories category = new Categories();
            category.setNameCategory(categoryName);
            return category;
        }).collect(Collectors.toList());
        product.setCategories(categoriesList);
        product.setImageName(this.imageName);
        product.setUploadedBy(this.toEntity().uploadedBy);
        return product;
    }
}
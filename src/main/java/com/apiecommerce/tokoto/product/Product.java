package com.apiecommerce.tokoto.product;

import com.apiecommerce.tokoto.categories.Categories;
import com.apiecommerce.tokoto.user.User;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "products")
public class Product {

    @ElementCollection(targetClass = Unit.class)
    @Enumerated(EnumType.STRING)
    private List<Unit> units;

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "name_product")
    private String title;

    private BigDecimal rating;

    private BigDecimal price;

    private Integer qty;

    private Integer sold;

    private Integer discount;

    @Column(length = 1000000)
    private String description;

    @Column(length = 1000000)
    private String details;

    @ManyToMany
    @JoinTable(
            name = "products_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Categories> categories;

    @ElementCollection
    @Column(name = "image_name")
    private List<String> imageName;

    @ElementCollection
    @Column(name = "image_type")
    private List<String> imageType;

    @Lob
    @ElementCollection
    @Column(name = "image_data", length = 1000000)
    private List<byte[]> imageData;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    public User uploadedBy;
}
package com.apiecommerce.tokoto.product;

import com.apiecommerce.tokoto.categories.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    /**
     *
     * @param keyword untuk mencari seluruh data dengan keyword yang sama dan terletak dibagian title
     * @return
     */
    List<Product> findAllByTitleContaining(String keyword);

    /**
     *
     * @param price
     * @return
     */
    List<Product> findAllByPrice(BigDecimal price);

    List<Product> findAllByCategoriesIn(List<Categories> categories);
}
package com.apiecommerce.tokoto.categories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    Categories findByNameCategory(String nameCategory);

	List<Categories> findByNameCategoryContaining(String keyword);
}

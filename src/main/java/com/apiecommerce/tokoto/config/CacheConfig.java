package com.apiecommerce.tokoto.config;

import com.apiecommerce.tokoto.product.Product;
import com.apiecommerce.tokoto.product.ProductResponse;
import com.apiecommerce.tokoto.product.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ProductService productService;

    @PostConstruct
    public void preloadCache() {
        Cache cache = cacheManager.getCache("tokotoCache");
        System.out.println("***** Intitializing cache" + cache);
        List<ProductResponse> productList = productService.findAllProduct();

        for (ProductResponse product : productList) {
            cache.put(product.getId(), product);
        }
    }
}

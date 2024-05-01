package com.apiecommerce.tokoto.product.supply;

import com.apiecommerce.tokoto.product.Product;
import com.apiecommerce.tokoto.product.ProductRequest;
import com.apiecommerce.tokoto.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class SupplyService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public SupplyResponse updatedSupply(String productId, SupplyRequest request) {
        Product product = productService.findById(productId).toEntity();

        Supplier supplier = new Supplier();
        supplier.setProduct(product);
        supplier.setQuantity(request.getQuantity());

        if (request.getQuantity() <= 0 || request.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity tidak boleh bernilai 0 / null");
        }

        supplier.setSupplyFrom(request.getSupplyFrom());
        supplier.setType(request.getType());
        supplier.setSupplyDate(LocalDateTime.now());

        supplyRepository.save(supplier);

        Integer quantitySupply = request.getQuantity() + product.getQty();

        productService.updateProduct(productId, ProductRequest.builder().qty(quantitySupply).build(), null);

        return toSupplyResponse(supplier);
    }

    @Transactional
    public SupplyResponse removeQuantity(String productId, SupplyRequest request) {
        Product product = productService.findById(productId).toEntity();

        Supplier supplier = new Supplier();
        supplier.setProduct(product);
        supplier.setQuantity(request.getQuantity());

        if (request.getQuantity() <= 0 || request.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity tidak boleh bernilai 0 / null");
        }

        supplier.setSupplyFrom(request.getSupplyFrom());
        supplier.setType(request.getType());
        supplier.setSupplyDate(LocalDateTime.now());

        supplyRepository.save(supplier);

        Integer quantitySupply = request.getQuantity() - product.getQty();

        productService.updateProduct(productId, ProductRequest.builder().qty(quantitySupply).build(), null);

        return toSupplyResponse(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplyResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Supplier> supplierPage = supplyRepository.findAll(pageable);
        return supplierPage.map(this::toSupplyResponse);
    }

    private SupplyResponse toSupplyResponse(Supplier supplier) {
        return SupplyResponse.builder()
                .id(supplier.getId())
                .product(supplier.getProduct().getTitle())
                .quantity(supplier.getQuantity())
                .supplyDate(supplier.getSupplyDate())
                .supply_type(supplier.getType().toString())
                .supplyFrom(supplier.getSupplyFrom())
                .build();
    }
}
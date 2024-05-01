package com.apiecommerce.tokoto.product.supply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SupplyRepository extends JpaRepository<Supplier, String> {

    Supplier findSupplierBySupplyDate(LocalDateTime supplyDate);

    Supplier findBySupplyFrom(String supplyFrom);
}
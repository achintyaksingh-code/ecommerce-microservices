package com.ecommerce.catalog.repository;

import com.ecommerce.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE p.active = true AND (:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<Product> searchActive(String q);
}

package com.ecommerce.catalog.service;

import com.ecommerce.catalog.api.CatalogDtos.CategoryRequest;
import com.ecommerce.catalog.api.CatalogDtos.ProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CatalogServiceTest {

    @Autowired
    private CatalogService catalogService;

    @Test
    void createCategoryAndProduct() {
        var category = catalogService.createCategory(new CategoryRequest("Electronics", "Gadgets"));
        var product = catalogService.createProduct(new ProductRequest(
                category.id(), "Phone", "Smartphone", new BigDecimal("499.99"), 10));
        assertThat(catalogService.getProduct(product.id()).name()).isEqualTo("Phone");
        assertThat(catalogService.searchProducts("pho")).hasSize(1);
    }
}

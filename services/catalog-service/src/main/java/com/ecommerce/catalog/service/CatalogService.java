package com.ecommerce.catalog.service;

import com.ecommerce.catalog.api.CatalogDtos.CategoryRequest;
import com.ecommerce.catalog.api.CatalogDtos.CategoryResponse;
import com.ecommerce.catalog.api.CatalogDtos.ProductRequest;
import com.ecommerce.catalog.api.CatalogDtos.ProductResponse;
import com.ecommerce.catalog.domain.Category;
import com.ecommerce.catalog.domain.Product;
import com.ecommerce.catalog.repository.CategoryRepository;
import com.ecommerce.catalog.repository.ProductRepository;
import com.ecommerce.common.web.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CatalogService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryRepository.save(new Category(request.name(), request.description()));
        return toCategory(category);
    }

    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll().stream().map(this::toCategory).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = productRepository.save(new Product(
                request.categoryId(), request.name(), request.description(), request.price(), request.stock()));
        return toProduct(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.update(request.name(), request.description(), request.price(), request.stock(), true);
        return toProduct(productRepository.save(product));
    }

    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id).map(this::toProduct)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public List<ProductResponse> searchProducts(String query) {
        return productRepository.searchActive(query).stream().map(this::toProduct).toList();
    }

    private CategoryResponse toCategory(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }

    private ProductResponse toProduct(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCurrency(),
                product.getStock(),
                product.isActive());
    }
}

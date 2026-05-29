package com.ecommerce.catalog.api;

import com.ecommerce.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogDtos.CategoryResponse createCategory(@Valid @RequestBody CatalogDtos.CategoryRequest request) {
        return catalogService.createCategory(request);
    }

    @GetMapping("/categories")
    public List<CatalogDtos.CategoryResponse> listCategories() {
        return catalogService.listCategories();
    }

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public CatalogDtos.ProductResponse createProduct(@Valid @RequestBody CatalogDtos.ProductRequest request) {
        return catalogService.createProduct(request);
    }

    @PutMapping("/products/{id}")
    public CatalogDtos.ProductResponse updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CatalogDtos.ProductRequest request) {
        return catalogService.updateProduct(id, request);
    }

    @GetMapping("/products/{id}")
    public CatalogDtos.ProductResponse getProduct(@PathVariable UUID id) {
        return catalogService.getProduct(id);
    }

    @GetMapping("/products")
    public List<CatalogDtos.ProductResponse> searchProducts(@RequestParam(required = false) String q) {
        return catalogService.searchProducts(q);
    }
}

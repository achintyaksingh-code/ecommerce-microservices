package com.ecommerce.catalog.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public final class CatalogDtos {
    private CatalogDtos() {
    }

    public record CategoryRequest(@NotBlank String name, String description) {
    }

    public record CategoryResponse(UUID id, String name, String description) {
    }

    public record ProductRequest(
            @NotNull UUID categoryId,
            @NotBlank String name,
            String description,
            @NotNull @DecimalMin("0.0") BigDecimal price,
            @Min(0) int stock
    ) {
    }

    public record ProductResponse(
            UUID id,
            UUID categoryId,
            String name,
            String description,
            BigDecimal price,
            String currency,
            int stock,
            boolean active
    ) {
    }
}

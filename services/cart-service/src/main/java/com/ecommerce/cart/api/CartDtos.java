package com.ecommerce.cart.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class CartDtos {
    private CartDtos() {
    }

    public record AddItemRequest(
            @NotNull UUID productId,
            @NotBlank String productName,
            @NotNull BigDecimal unitPrice,
            @Min(1) int quantity
    ) {
    }

    public record UpdateItemRequest(@Min(1) int quantity) {
    }

    public record CartItemResponse(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
    }

    public record CartResponse(UUID cartId, String userId, List<CartItemResponse> items, BigDecimal total) {
    }
}

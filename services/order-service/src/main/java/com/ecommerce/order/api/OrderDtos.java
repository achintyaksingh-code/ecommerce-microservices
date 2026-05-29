package com.ecommerce.order.api;

import com.ecommerce.order.domain.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record CreateOrderItem(
            @NotNull UUID productId,
            @NotBlank String productName,
            @NotNull BigDecimal unitPrice,
            @Min(1) int quantity
    ) {
    }

    public record CreateOrderRequest(@NotEmpty @Valid List<CreateOrderItem> items) {
    }

    public record OrderItemResponse(UUID productId, String productName, BigDecimal unitPrice, int quantity) {
    }

    public record OrderResponse(
            UUID id,
            String userId,
            OrderStatus status,
            BigDecimal totalAmount,
            String currency,
            List<OrderItemResponse> items
    ) {
    }
}

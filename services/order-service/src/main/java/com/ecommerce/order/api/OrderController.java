package com.ecommerce.order.api;

import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDtos.OrderResponse createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody OrderDtos.CreateOrderRequest request) {
        return orderService.createOrder(userId, request, idempotencyKey);
    }

    @GetMapping("/{orderId}")
    public OrderDtos.OrderResponse getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }
}

package com.ecommerce.order.service;

import com.ecommerce.common.events.EventTypes;
import com.ecommerce.common.outbox.OutboxPublisher;
import com.ecommerce.common.web.ResourceNotFoundException;
import com.ecommerce.order.api.OrderDtos.CreateOrderRequest;
import com.ecommerce.order.api.OrderDtos.OrderItemResponse;
import com.ecommerce.order.api.OrderDtos.OrderResponse;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderItem;
import com.ecommerce.order.events.OrderCreatedEvent;
import com.ecommerce.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OutboxPublisher outboxPublisher;

    public OrderService(OrderRepository orderRepository, OutboxPublisher outboxPublisher) {
        this.orderRepository = orderRepository;
        this.outboxPublisher = outboxPublisher;
    }

    @Transactional
    public OrderResponse createOrder(String userId, CreateOrderRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return orderRepository.findByIdempotencyKey(idempotencyKey)
                    .map(this::toResponse)
                    .orElseGet(() -> createAndPublish(userId, request, idempotencyKey));
        }
        return createAndPublish(userId, request, null);
    }

    public OrderResponse getOrder(UUID orderId) {
        return orderRepository.findById(orderId).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private OrderResponse createAndPublish(String userId, CreateOrderRequest request, String idempotencyKey) {
        BigDecimal total = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order(userId, total, idempotencyKey);
        for (var item : request.items()) {
            order.addItem(new OrderItem(
                    order, item.productId(), item.productName(), item.unitPrice(), item.quantity()));
        }
        Order saved = orderRepository.save(order);
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getUserId(),
                saved.getTotalAmount(),
                saved.getCurrency(),
                Instant.now());
        outboxPublisher.enqueue("Order", saved.getId().toString(), EventTypes.ORDER_CREATED, event);
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCurrency(),
                order.getItems().stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(), item.getProductName(), item.getUnitPrice(), item.getQuantity()))
                        .toList());
    }
}

package com.ecommerce.order.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.CREATED;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(nullable = false)
    private String currency = "USD";
    @Column(name = "idempotency_key")
    private String idempotencyKey;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(String userId, BigDecimal totalAmount, String idempotencyKey) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public void markPaid() {
        this.status = OrderStatus.PAID;
        touch();
    }

    public void markFailed() {
        this.status = OrderStatus.FAILED;
        touch();
    }

    public void markCancelled() {
        this.status = OrderStatus.CANCELLED;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}

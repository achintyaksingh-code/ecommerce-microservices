package com.ecommerce.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String currency = "USD";
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "idempotency_key")
    private String idempotencyKey;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    protected Payment() {
    }

    public Payment(UUID orderId, BigDecimal amount, String currency, String idempotencyKey) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void markConfirmed() {
        this.status = PaymentStatus.CONFIRMED;
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
    }
}

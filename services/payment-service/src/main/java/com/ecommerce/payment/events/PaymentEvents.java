package com.ecommerce.payment.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class PaymentEvents {
    private PaymentEvents() {
    }

    public record OrderCreatedEvent(
            UUID eventId,
            UUID orderId,
            String userId,
            BigDecimal totalAmount,
            String currency,
            Instant occurredAt
    ) {
    }

    public record PaymentConfirmedEvent(
            UUID eventId,
            UUID paymentId,
            UUID orderId,
            BigDecimal amount,
            String currency,
            Instant occurredAt
    ) {
    }

    public record PaymentFailedEvent(
            UUID eventId,
            UUID paymentId,
            UUID orderId,
            String reason,
            Instant occurredAt
    ) {
    }
}

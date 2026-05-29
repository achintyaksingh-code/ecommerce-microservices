package com.ecommerce.order.events;

import java.util.UUID;

public record PaymentEventPayload(
        UUID eventId,
        UUID paymentId,
        UUID orderId,
        String reason
) {
}

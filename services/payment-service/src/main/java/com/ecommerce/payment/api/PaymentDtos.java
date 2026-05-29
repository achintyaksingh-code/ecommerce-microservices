package com.ecommerce.payment.api;

import com.ecommerce.payment.domain.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public final class PaymentDtos {
    private PaymentDtos() {
    }

    public record CreatePaymentRequest(@NotNull UUID orderId, @NotNull BigDecimal amount, String currency) {
    }

    public record PaymentResponse(
            UUID id,
            UUID orderId,
            BigDecimal amount,
            String currency,
            PaymentStatus status
    ) {
    }
}

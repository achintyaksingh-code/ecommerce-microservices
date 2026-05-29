package com.ecommerce.payment.service;

import com.ecommerce.common.events.EventTypes;
import com.ecommerce.common.outbox.OutboxPublisher;
import com.ecommerce.common.web.BusinessException;
import com.ecommerce.common.web.ResourceNotFoundException;
import com.ecommerce.payment.api.PaymentDtos.CreatePaymentRequest;
import com.ecommerce.payment.api.PaymentDtos.PaymentResponse;
import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.events.PaymentEvents.PaymentConfirmedEvent;
import com.ecommerce.payment.events.PaymentEvents.PaymentFailedEvent;
import com.ecommerce.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {
    private static final Logger auditLog = LoggerFactory.getLogger("AUDIT_PAYMENT");

    private final PaymentRepository paymentRepository;
    private final OutboxPublisher outboxPublisher;
    private final double mockFailureRate;

    public PaymentService(
            PaymentRepository paymentRepository,
            OutboxPublisher outboxPublisher,
            @Value("${ecommerce.payment.mock-failure-rate:0.1}") double mockFailureRate) {
        this.paymentRepository = paymentRepository;
        this.outboxPublisher = outboxPublisher;
        this.mockFailureRate = mockFailureRate;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return paymentRepository.findByIdempotencyKey(idempotencyKey)
                    .map(this::toResponse)
                    .orElseGet(() -> processPayment(request, idempotencyKey));
        }
        return processPayment(request, null);
    }

    public PaymentResponse getPayment(UUID paymentId) {
        return paymentRepository.findById(paymentId).map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    @Transactional
    public void processOrderCreated(UUID orderId, java.math.BigDecimal amount, String currency) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            return;
        }
        Payment payment = paymentRepository.save(new Payment(orderId, amount, currency, "order-" + orderId));
        publishPaymentResult(payment, amount, currency);
    }

    private PaymentResponse processPayment(CreatePaymentRequest request, String idempotencyKey) {
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, "Payment already exists for order");
        }
        String currency = request.currency() == null ? "USD" : request.currency();
        Payment payment = new Payment(request.orderId(), request.amount(), currency, idempotencyKey);
        paymentRepository.save(payment);
        publishPaymentResult(payment, request.amount(), currency);
        return toResponse(paymentRepository.findById(payment.getId()).orElseThrow());
    }

    private void publishPaymentResult(Payment payment, java.math.BigDecimal amount, String currency) {
        boolean failed = ThreadLocalRandom.current().nextDouble() < mockFailureRate;
        if (failed) {
            payment.markFailed();
            paymentRepository.save(payment);
            PaymentFailedEvent event = new PaymentFailedEvent(
                    UUID.randomUUID(), payment.getId(), payment.getOrderId(), "Mock provider declined", Instant.now());
            outboxPublisher.enqueue("Payment", payment.getId().toString(), EventTypes.PAYMENT_FAILED, event);
            auditLog.warn("payment_failed paymentId={} orderId={}", payment.getId(), payment.getOrderId());
            return;
        }
        payment.markConfirmed();
        paymentRepository.save(payment);
        PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                UUID.randomUUID(), payment.getId(), payment.getOrderId(), amount, currency, Instant.now());
        outboxPublisher.enqueue("Payment", payment.getId().toString(), EventTypes.PAYMENT_CONFIRMED, event);
        auditLog.info("payment_confirmed paymentId={} orderId={}", payment.getId(), payment.getOrderId());
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(), payment.getOrderId(), payment.getAmount(), payment.getCurrency(), payment.getStatus());
    }
}

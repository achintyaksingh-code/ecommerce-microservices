package com.ecommerce.order.service;

import com.ecommerce.common.events.EventTypes;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.ProcessedEvent;
import com.ecommerce.order.events.PaymentEventPayload;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(OrderEventProcessor.class);

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String dlqTopic;

    public OrderEventProcessor(
            OrderRepository orderRepository,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${ecommerce.kafka.dlq-topic}") String dlqTopic) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.dlqTopic = dlqTopic;
    }

    @KafkaListener(topics = "${ecommerce.kafka.topic}", groupId = "order-service")
    @Transactional
    public void onEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("eventType").asText();
            if (eventType.isBlank()) {
                eventType = inferType(root);
            }
            UUID eventId = UUID.fromString(root.path("eventId").asText());
            if (processedEventRepository.existsById(eventId)) {
                return;
            }
            if (EventTypes.ORDER_CREATED.equals(eventType)) {
                processedEventRepository.save(new ProcessedEvent(eventId));
                return;
            }
            if (EventTypes.PAYMENT_CONFIRMED.equals(eventType)) {
                PaymentEventPayload event = objectMapper.treeToValue(root, PaymentEventPayload.class);
                handlePaymentConfirmed(event);
            } else if (EventTypes.PAYMENT_FAILED.equals(eventType)) {
                PaymentEventPayload event = objectMapper.treeToValue(root, PaymentEventPayload.class);
                handlePaymentFailed(event);
            }
            processedEventRepository.save(new ProcessedEvent(eventId));
        } catch (Exception ex) {
            log.error("Failed to process event payload={}", payload, ex);
            kafkaTemplate.send(dlqTopic, payload);
        }
    }

    private String inferType(JsonNode root) {
        if (root.has("paymentId") && root.has("orderId") && root.has("reason")) {
            return EventTypes.PAYMENT_FAILED;
        }
        if (root.has("paymentId") && root.has("orderId") && root.has("amount")) {
            return EventTypes.PAYMENT_CONFIRMED;
        }
        return "";
    }

    private void handlePaymentConfirmed(PaymentEventPayload event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found for payment event"));
        order.markPaid();
        orderRepository.save(order);
    }

    private void handlePaymentFailed(PaymentEventPayload event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new IllegalStateException("Order not found for payment event"));
        order.markFailed();
        orderRepository.save(order);
    }
}

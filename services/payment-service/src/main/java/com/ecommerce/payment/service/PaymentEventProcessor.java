package com.ecommerce.payment.service;

import com.ecommerce.common.events.EventTypes;
import com.ecommerce.payment.domain.ProcessedEvent;
import com.ecommerce.payment.events.PaymentEvents.OrderCreatedEvent;
import com.ecommerce.payment.repository.ProcessedEventRepository;
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
public class PaymentEventProcessor {
    private static final Logger log = LoggerFactory.getLogger(PaymentEventProcessor.class);

    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String dlqTopic;

    public PaymentEventProcessor(
            PaymentService paymentService,
            ProcessedEventRepository processedEventRepository,
            ObjectMapper objectMapper,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${ecommerce.kafka.dlq-topic}") String dlqTopic) {
        this.paymentService = paymentService;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.dlqTopic = dlqTopic;
    }

    @KafkaListener(topics = "${ecommerce.kafka.topic}", groupId = "payment-service")
    @Transactional
    public void onEvent(String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            String eventType = root.path("eventType").asText(EventTypes.ORDER_CREATED);
            UUID eventId = UUID.fromString(root.path("eventId").asText());
            if (processedEventRepository.existsById(eventId)) {
                return;
            }
            if (EventTypes.ORDER_CREATED.equals(eventType)) {
                OrderCreatedEvent event = objectMapper.treeToValue(root, OrderCreatedEvent.class);
                paymentService.processOrderCreated(event.orderId(), event.totalAmount(), event.currency());
            }
            processedEventRepository.save(new ProcessedEvent(eventId));
        } catch (Exception ex) {
            log.error("Failed to process payment event payload={}", payload, ex);
            kafkaTemplate.send(dlqTopic, payload);
        }
    }
}

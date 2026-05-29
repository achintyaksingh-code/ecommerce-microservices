package com.ecommerce.common.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic;

    public OutboxPublisher(
            OutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            String topic) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${ecommerce.outbox.poll-interval-ms:2000}")
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : repository.findUnpublished()) {
            try {
                String envelope = buildEnvelope(event.getEventType(), event.getPayload());
                kafkaTemplate.send(topic, event.getAggregateId(), envelope);
                event.markPublished();
                repository.save(event);
            } catch (Exception ex) {
                log.error("Failed to publish outbox event {}", event.getId(), ex);
            }
        }
    }

    private String buildEnvelope(String eventType, String payloadJson) throws Exception {
        var root = objectMapper.createObjectNode();
        root.put("eventType", eventType);
        root.setAll((com.fasterxml.jackson.databind.node.ObjectNode) objectMapper.readTree(payloadJson));
        return objectMapper.writeValueAsString(root);
    }

    public void enqueue(String aggregateType, String aggregateId, String eventType, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            repository.save(new OutboxEvent(aggregateType, aggregateId, eventType, json));
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to serialize outbox payload", ex);
        }
    }
}

package com.ecommerce.payment.config;

import com.ecommerce.common.outbox.OutboxPublisher;
import com.ecommerce.common.outbox.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class PaymentKafkaConfig {

    @Bean
    public OutboxPublisher outboxPublisher(
            OutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${ecommerce.kafka.topic}") String topic) {
        return new OutboxPublisher(repository, kafkaTemplate, objectMapper, topic);
    }
}

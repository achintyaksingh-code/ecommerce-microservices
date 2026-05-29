package com.ecommerce.order.config;

import com.ecommerce.common.outbox.OutboxPublisher;
import com.ecommerce.common.outbox.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class OrderKafkaConfig {

    @Bean
    public NewTopic ecommerceEventsTopic(@Value("${ecommerce.kafka.topic}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic ecommerceEventsDlqTopic(@Value("${ecommerce.kafka.dlq-topic}") String topic) {
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }

    @Bean
    public OutboxPublisher outboxPublisher(
            OutboxRepository repository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${ecommerce.kafka.topic}") String topic) {
        return new OutboxPublisher(repository, kafkaTemplate, objectMapper, topic);
    }
}

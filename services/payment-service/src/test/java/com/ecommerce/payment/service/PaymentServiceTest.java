package com.ecommerce.payment.service;

import com.ecommerce.payment.api.PaymentDtos.CreatePaymentRequest;
import com.ecommerce.payment.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "ecommerce.payment.mock-failure-rate=0.0",
        "spring.kafka.listener.auto-startup=false",
        "spring.task.scheduling.enabled=false"
})
@Testcontainers
class PaymentServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("payment_db")
            .withUsername("ecommerce")
            .withPassword("ecommerce");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.cloud.config.enabled", () -> "false");
        registry.add("eureka.client.enabled", () -> "false");
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Autowired
    private PaymentService paymentService;

    @Test
    void createPaymentConfirms() {
        UUID orderId = UUID.randomUUID();
        var response = paymentService.createPayment(
                new CreatePaymentRequest(orderId, new BigDecimal("42.00"), "USD"), "pay-idem-1");
        assertThat(response.status()).isEqualTo(PaymentStatus.CONFIRMED);
    }
}

package com.ecommerce.order.service;

import com.ecommerce.order.api.OrderDtos.CreateOrderItem;
import com.ecommerce.order.api.OrderDtos.CreateOrderRequest;
import com.ecommerce.order.domain.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.kafka.listener.auto-startup=false",
        "spring.task.scheduling.enabled=false"
})
@Testcontainers
class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("order_db")
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
    private OrderService orderService;

    @Test
    void createOrderIsIdempotent() {
        var request = new CreateOrderRequest(List.of(
                new CreateOrderItem(UUID.randomUUID(), "Phone", new BigDecimal("100.00"), 1)));
        var first = orderService.createOrder("user-1", request, "idem-1");
        var second = orderService.createOrder("user-1", request, "idem-1");
        assertThat(first.id()).isEqualTo(second.id());
        assertThat(first.status()).isEqualTo(OrderStatus.CREATED);
    }
}

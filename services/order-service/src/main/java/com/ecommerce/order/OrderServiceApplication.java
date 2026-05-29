package com.ecommerce.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.ecommerce.order", "com.ecommerce.common"})
@EntityScan(basePackages = {"com.ecommerce.order", "com.ecommerce.common.outbox"})
@EnableJpaRepositories(basePackages = {"com.ecommerce.order", "com.ecommerce.common.outbox"})
@EnableDiscoveryClient
@EnableScheduling
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

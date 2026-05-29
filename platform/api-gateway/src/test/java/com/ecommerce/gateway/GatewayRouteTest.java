package com.ecommerce.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false"
})
class GatewayRouteTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void exposesCoreServiceRoutes() {
        StepVerifier.create(routeLocator.getRoutes().collectList())
                .assertNext(routes -> assertThat(routes)
                        .extracting(route -> route.getId())
                        .contains("auth-service", "catalog-service", "order-service", "payment-service"))
                .verifyComplete();
    }
}

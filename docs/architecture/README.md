# Architecture Overview

## Services

| Service | Port | Database | Responsibility |
|---------|------|----------|----------------|
| config-server | 8888 | - | Centralized configuration |
| discovery-server | 8761 | - | Eureka service registry |
| api-gateway | 8080 | - | Edge routing |
| auth-service | 8081 | auth_db | Registration, login, JWT |
| catalog-service | 8082 | catalog_db | Products and categories |
| cart-service | 8083 | cart_db | Shopping cart + Redis cache |
| order-service | 8084 | order_db | Orders + outbox events |
| payment-service | 8085 | payment_db | Mock payments + outbox events |

## Event Flow

1. `order-service` creates an order and writes `order.created` to outbox.
2. Outbox publisher emits to Kafka topic `ecommerce-events`.
3. `payment-service` consumes `order.created` and processes mock payment.
4. `payment-service` emits `payment.confirmed` or `payment.failed`.
5. `order-service` updates order status based on payment events.

## Consistency

- Database-per-service with Flyway migrations.
- Outbox pattern for reliable event publication.
- Idempotency keys on order and payment creation APIs.
- DLQ topic `ecommerce-events-dlq` for poison messages.

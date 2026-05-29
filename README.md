# E-commerce Microservices (Spring Boot MVP)

Java 21 + Spring Boot 3.3 microservices platform for an MVP e-commerce flow:

register/login → browse catalog → manage cart → place order → mock payment.

## Stack

- Spring Boot, Spring Cloud Gateway, Eureka, Config Server
- PostgreSQL (database-per-service), Redis, Kafka
- Outbox pattern, idempotency keys, DLQ handling
- Prometheus + Grafana, Kubernetes Helm chart, GitHub Actions CI

## Project Layout

```
platform/          # config-server, discovery-server, api-gateway
services/          # auth, catalog, cart, order, payment
shared/            # common-lib + event contracts
infra/docker/      # local dependencies
infra/k8s/helm/    # Kubernetes deployment
```

## Quick Start (Local)

1. Start infrastructure:

```powershell
.\scripts\start-infra.ps1
```

2. Build all services (uses Maven Wrapper — no global Maven install required):

```powershell
.\scripts\build-all.ps1
```

Or directly:

```powershell
.\mvnw.cmd -B clean verify
```

On Linux/macOS use `./mvnw` instead of `.\mvnw.cmd`.

3. Run platform services (separate terminals):

```powershell
.\mvnw.cmd -pl platform/discovery-server spring-boot:run
.\mvnw.cmd -pl platform/config-server spring-boot:run
.\mvnw.cmd -pl platform/api-gateway spring-boot:run
```

4. Run business services:

```powershell
.\mvnw.cmd -pl services/auth-service spring-boot:run
.\mvnw.cmd -pl services/catalog-service spring-boot:run
.\mvnw.cmd -pl services/cart-service spring-boot:run
.\mvnw.cmd -pl services/order-service spring-boot:run
.\mvnw.cmd -pl services/payment-service spring-boot:run
```

## API Examples (via Gateway `http://localhost:8080`)

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/catalog/products`
- `GET /api/v1/carts/me` (header `X-User-Id`)
- `POST /api/v1/orders` (headers `X-User-Id`, optional `Idempotency-Key`)
- `POST /api/v1/payments`

## Kubernetes

```bash
helm upgrade --install ecommerce ./infra/k8s/helm/ecommerce -n ecommerce --create-namespace
```

## Testing

```powershell
.\mvnw.cmd clean verify
```

Includes unit tests and Testcontainers integration tests for core services.

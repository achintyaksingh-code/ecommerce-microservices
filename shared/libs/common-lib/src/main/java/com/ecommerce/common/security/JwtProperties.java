package com.ecommerce.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ecommerce.security.jwt")
public record JwtProperties(
        String secret,
        long accessTokenExpirationMs,
        long refreshTokenExpirationMs
) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            secret = "change-me-in-production-use-at-least-256-bits-secret-key!!";
        }
        if (accessTokenExpirationMs <= 0) {
            accessTokenExpirationMs = 900_000L;
        }
        if (refreshTokenExpirationMs <= 0) {
            refreshTokenExpirationMs = 2_592_000_000L;
        }
    }
}

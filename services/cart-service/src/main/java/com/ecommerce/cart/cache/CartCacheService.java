package com.ecommerce.cart.cache;

import com.ecommerce.cart.api.CartDtos.CartResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class CartCacheService {
    private static final Duration TTL = Duration.ofMinutes(15);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CartCacheService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void put(String userId, CartResponse cart) {
        try {
            redisTemplate.opsForValue().set(key(userId), objectMapper.writeValueAsString(cart), TTL);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to cache cart", ex);
        }
    }

    public Optional<CartResponse> get(String userId) {
        String json = redisTemplate.opsForValue().get(key(userId));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, CartResponse.class));
        } catch (JsonProcessingException ex) {
            return Optional.empty();
        }
    }

    public void evict(String userId) {
        redisTemplate.delete(key(userId));
    }

    private String key(String userId) {
        return "cart:" + userId;
    }
}

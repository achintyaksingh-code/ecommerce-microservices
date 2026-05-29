package com.ecommerce.cart.repository;

import com.ecommerce.cart.domain.Cart;
import com.ecommerce.cart.domain.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatus(String userId, CartStatus status);
}

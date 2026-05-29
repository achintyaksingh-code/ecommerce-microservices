package com.ecommerce.cart.service;

import com.ecommerce.cart.api.CartDtos.AddItemRequest;
import com.ecommerce.cart.api.CartDtos.CartItemResponse;
import com.ecommerce.cart.api.CartDtos.CartResponse;
import com.ecommerce.cart.api.CartDtos.UpdateItemRequest;
import com.ecommerce.cart.cache.CartCacheService;
import com.ecommerce.cart.domain.Cart;
import com.ecommerce.cart.domain.CartItem;
import com.ecommerce.cart.domain.CartStatus;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.common.web.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartCacheService cartCacheService;

    public CartService(CartRepository cartRepository, CartCacheService cartCacheService) {
        this.cartRepository = cartRepository;
        this.cartCacheService = cartCacheService;
    }

    @Transactional
    public CartResponse getActiveCart(String userId) {
        return cartCacheService.get(userId).orElseGet(() -> {
            CartResponse response = toResponse(findOrCreateCart(userId));
            cartCacheService.put(userId, response);
            return response;
        });
    }

    @Transactional
    public CartResponse addItem(String userId, AddItemRequest request) {
        Cart cart = findOrCreateCart(userId);
        CartItem existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst()
                .orElse(null);
        if (existing == null) {
            cart.getItems().add(new CartItem(
                    cart, request.productId(), request.productName(), request.unitPrice(), request.quantity()));
        } else {
            existing.setQuantity(existing.getQuantity() + request.quantity());
        }
        cart.touch();
        CartResponse response = toResponse(cartRepository.save(cart));
        cartCacheService.put(userId, response);
        return response;
    }

    @Transactional
    public CartResponse updateItem(String userId, UUID productId, UpdateItemRequest request) {
        Cart cart = findOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        item.setQuantity(request.quantity());
        cart.touch();
        CartResponse response = toResponse(cartRepository.save(cart));
        cartCacheService.put(userId, response);
        return response;
    }

    @Transactional
    public CartResponse removeItem(String userId, UUID productId) {
        Cart cart = findOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cart.touch();
        CartResponse response = toResponse(cartRepository.save(cart));
        cartCacheService.put(userId, response);
        return response;
    }

    @Transactional
    public void checkoutCart(String userId) {
        Cart cart = findOrCreateCart(userId);
        cart.checkout();
        cartRepository.save(cart);
        cartCacheService.evict(userId);
    }

    private Cart findOrCreateCart(String userId) {
        return cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));
    }

    private CartResponse toResponse(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getItems().stream()
                        .map(item -> new CartItemResponse(
                                item.getProductId(), item.getProductName(), item.getUnitPrice(), item.getQuantity()))
                        .toList(),
                total);
    }
}

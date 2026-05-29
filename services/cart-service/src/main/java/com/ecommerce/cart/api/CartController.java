package com.ecommerce.cart.api;

import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/me")
    public CartDtos.CartResponse getMyCart(@RequestHeader("X-User-Id") String userId) {
        return cartService.getActiveCart(userId);
    }

    @PostMapping("/me/items")
    public CartDtos.CartResponse addItem(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CartDtos.AddItemRequest request) {
        return cartService.addItem(userId, request);
    }

    @PutMapping("/me/items/{productId}")
    public CartDtos.CartResponse updateItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID productId,
            @Valid @RequestBody CartDtos.UpdateItemRequest request) {
        return cartService.updateItem(userId, productId, request);
    }

    @DeleteMapping("/me/items/{productId}")
    public CartDtos.CartResponse removeItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID productId) {
        return cartService.removeItem(userId, productId);
    }
}

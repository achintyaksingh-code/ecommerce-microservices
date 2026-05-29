package com.ecommerce.cart.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "carts")
public class Cart {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartStatus status = CartStatus.ACTIVE;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {
    }

    public Cart(String userId) {
        this.userId = userId;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    public void checkout() {
        this.status = CartStatus.CHECKED_OUT;
        touch();
    }
}

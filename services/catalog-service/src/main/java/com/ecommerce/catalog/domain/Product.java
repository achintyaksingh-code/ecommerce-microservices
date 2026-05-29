package com.ecommerce.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
public class Product {
    @Id
    private UUID id = UUID.randomUUID();
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private String currency = "USD";
    @Column(nullable = false)
    private int stock;
    @Column(nullable = false)
    private boolean active = true;

    protected Product() {
    }

    public Product(UUID categoryId, String name, String description, BigDecimal price, int stock) {
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public int getStock() {
        return stock;
    }

    public boolean isActive() {
        return active;
    }

    public void update(String name, String description, BigDecimal price, int stock, boolean active) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.active = active;
    }
}

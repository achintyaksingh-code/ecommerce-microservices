package com.ecommerce.order.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    private UUID id = UUID.randomUUID();
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    @Column(name = "product_name", nullable = false)
    private String productName;
    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
    @Column(nullable = false)
    private int quantity;

    protected OrderItem() {
    }

    public OrderItem(Order order, UUID productId, String productName, BigDecimal unitPrice, int quantity) {
        this.order = order;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}

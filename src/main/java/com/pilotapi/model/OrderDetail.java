package com.pilotapi.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "[Order Details]")
public class OrderDetail {

    @EmbeddedId
    private OrderDetailId id;

    @Column(name = "Discount")
    private Float discount;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "UnitPrice")
    private Double unitPrice;

    public OrderDetailId getId() {
        return id;
    }

    public void setId(OrderDetailId id) {
        this.id = id;
    }

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }
}

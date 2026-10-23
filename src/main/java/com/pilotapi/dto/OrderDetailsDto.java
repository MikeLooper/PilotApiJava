package com.pilotapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class OrderDetailsDto {

    @NotNull
    private Float discount;

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer productId;

    @NotNull
    private Integer quantity;

    @NotNull
    private BigDecimal unitPrice;

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

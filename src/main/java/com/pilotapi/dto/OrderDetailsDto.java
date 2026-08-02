package com.pilotapi.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class OrderDetailsDto {

    @NotNull
    private Float discount;

    @NotNull
    private Integer orderID;

    @NotNull
    private Integer productID;

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

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
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

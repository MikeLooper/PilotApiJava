package com.pilotapi.dto;

public class AddResponseOrderDetailsDto {

    private Integer orderId;
    private Integer productId;

    public AddResponseOrderDetailsDto() {
    }

    public AddResponseOrderDetailsDto(Integer orderId, Integer productId) {
        this.orderId = orderId;
        this.productId = productId;
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
}

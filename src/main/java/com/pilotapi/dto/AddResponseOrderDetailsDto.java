package com.pilotapi.dto;

public class AddResponseOrderDetailsDto {

    private Integer orderID;
    private Integer productID;

    public AddResponseOrderDetailsDto() {
    }

    public AddResponseOrderDetailsDto(Integer orderID, Integer productID) {
        this.orderID = orderID;
        this.productID = productID;
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
}

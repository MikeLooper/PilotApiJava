package com.pilotapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrderDetailId implements Serializable {

    @Column(name = "ProductID")
    private Integer productID;

    @Column(name = "OrderID")
    private Integer orderID;

    public OrderDetailId() {
    }

    public OrderDetailId(Integer productID, Integer orderID) {
        this.productID = productID;
        this.orderID = orderID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDetailId that = (OrderDetailId) o;
        return Objects.equals(productID, that.productID) && Objects.equals(orderID, that.orderID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productID, orderID);
    }
}

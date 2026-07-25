package com.pilotapi.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class OrdersDto {

    private String customerID;
    private Integer employeeID;
    private Double freight;
    private OffsetDateTime orderDate;

    @NotNull
    private Integer orderID;

    private OffsetDateTime requiredDate;
    private String shipAddress;
    private String shipCity;
    private String shipCountry;
    private String shipName;
    private OffsetDateTime shippedDate;
    private String shipPostalCode;
    private String shipRegion;
    private Integer shipVia;

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public Integer getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(Integer employeeID) {
        this.employeeID = employeeID;
    }

    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    public OffsetDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(OffsetDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public OffsetDateTime getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(OffsetDateTime requiredDate) {
        this.requiredDate = requiredDate;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getShipCity() {
        return shipCity;
    }

    public void setShipCity(String shipCity) {
        this.shipCity = shipCity;
    }

    public String getShipCountry() {
        return shipCountry;
    }

    public void setShipCountry(String shipCountry) {
        this.shipCountry = shipCountry;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public OffsetDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(OffsetDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getShipPostalCode() {
        return shipPostalCode;
    }

    public void setShipPostalCode(String shipPostalCode) {
        this.shipPostalCode = shipPostalCode;
    }

    public String getShipRegion() {
        return shipRegion;
    }

    public void setShipRegion(String shipRegion) {
        this.shipRegion = shipRegion;
    }

    public Integer getShipVia() {
        return shipVia;
    }

    public void setShipVia(Integer shipVia) {
        this.shipVia = shipVia;
    }
}

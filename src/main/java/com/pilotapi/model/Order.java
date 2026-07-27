package com.pilotapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "Orders")
public class Order {

    @Column(name = "CustomerID")
    private String customerID;

    @Column(name = "EmployeeID")
    private Integer employeeID;

    @Column(name = "Freight")
    private Double freight;

    @Column(name = "OrderDate")
    private LocalDateTime orderDate;

    @Id
    @Column(name = "OrderID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderID;

    @Column(name = "RequiredDate")
    private LocalDateTime requiredDate;

    @Column(name = "ShipAddress")
    private String shipAddress;

    @Column(name = "ShipCity")
    private String shipCity;

    @Column(name = "ShipCountry")
    private String shipCountry;

    @Column(name = "ShipName")
    private String shipName;

    @Column(name = "ShippedDate")
    private LocalDateTime shippedDate;

    @Column(name = "ShipPostalCode")
    private String shipPostalCode;

    @Column(name = "ShipRegion")
    private String shipRegion;

    @Column(name = "ShipVia")
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public LocalDateTime getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(LocalDateTime requiredDate) {
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

    public LocalDateTime getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
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

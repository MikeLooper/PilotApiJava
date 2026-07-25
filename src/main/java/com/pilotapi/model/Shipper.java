package com.pilotapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "Shippers")
public class Shipper {

    @Column(name = "CompanyName")
    private String companyName;

    @Column(name = "Phone")
    private String phone;

    @Id
    @Column(name = "ShipperID")
    private Integer shipperID;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getShipperID() {
        return shipperID;
    }

    public void setShipperID(Integer shipperID) {
        this.shipperID = shipperID;
    }
}

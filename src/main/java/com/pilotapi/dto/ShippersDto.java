package com.pilotapi.dto;

import jakarta.validation.constraints.NotBlank;
import io.micrometer.common.lang.Nullable;

public class ShippersDto {

    @NotBlank
    private String companyName;

    private String phone;

    @Nullable
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

package com.pilotapi.dto;

import jakarta.validation.constraints.NotBlank;
import io.micrometer.common.lang.Nullable;

public class ShippersDto {

    @NotBlank
    private String companyName;

    private String phone;

    @Nullable
    private Integer shipperId;

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

    public Integer getShipperId() {
        return shipperId;
    }

    public void setShipperId(Integer shipperId) {
        this.shipperId = shipperId;
    }
}

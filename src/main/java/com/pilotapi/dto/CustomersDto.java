package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CustomersDto {

    @Schema(types = {"null", "string"})
    @Size(max = 60)
    private String address;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String city;

    @Schema(types = {"string"})
    @NotBlank
    @Size(min = 1, max = 40)
    private String companyName;

    @Schema(types = {"null", "string"})
    @Size(max = 30)
    private String contactName;

    @Schema(types = {"null", "string"})
    @Size(max = 30)
    private String contactTitle;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String country;

    @Schema(types = {"string"})
    @NotBlank
    @Size(min = OpenApiConfig.CUSTOMER_ID_LENGTH, max = OpenApiConfig.CUSTOMER_ID_LENGTH)
    private String customerId;

    @Schema(types = {"null", "string"})
    @Size(max = 24)
    private String fax;

    @Schema(types = {"null", "string"})
    @Size(max = 24)
    private String phone;

    @Schema(types = {"null", "string"})
    @Size(max = 10)
    private String postalCode;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String region;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTitle() {
        return contactTitle;
    }

    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}

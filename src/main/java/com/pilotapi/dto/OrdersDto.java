package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import com.pilotapi.validation.SqlServerDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.micrometer.common.lang.Nullable;

public class OrdersDto {

    @Schema(types = {"null", "string"})
    @Size(min = OpenApiConfig.CUSTOMER_ID_LENGTH, max = OpenApiConfig.CUSTOMER_ID_LENGTH)
    private String customerId;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @Min(1)
    private Integer employeeId;

    @Schema(types = {"null", "number", "string"}, format = "double", pattern = OpenApiConfig.DECIMAL_PATTERN,
        minimum = OpenApiConfig.MONEY_MINIMUM, maximum = OpenApiConfig.MONEY_MAXIMUM,
        description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @DecimalMin(OpenApiConfig.MONEY_MINIMUM)
    @DecimalMax(OpenApiConfig.MONEY_MAXIMUM)
    @Digits(integer = 15, fraction = 4)
    private BigDecimal freight;

    @Schema(types = {"null", "string"}, format = "date-time", description = OpenApiConfig.DATETIME_RANGE_DESCRIPTION)
    @SqlServerDateTime
    private LocalDateTime orderDate;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @Min(1)
    @Nullable
    private Integer orderId;

    @Schema(types = {"null", "string"}, format = "date-time", description = OpenApiConfig.DATETIME_RANGE_DESCRIPTION)
    @SqlServerDateTime
    private LocalDateTime requiredDate;

    @Schema(types = {"null", "string"})
    @Size(max = 60)
    private String shipAddress;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String shipCity;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String shipCountry;

    @Schema(types = {"null", "string"})
    @Size(max = 40)
    private String shipName;

    @Schema(types = {"null", "string"}, format = "date-time", description = OpenApiConfig.DATETIME_RANGE_DESCRIPTION)
    @SqlServerDateTime
    private LocalDateTime shippedDate;

    @Schema(types = {"null", "string"})
    @Size(max = 10)
    private String shipPostalCode;

    @Schema(types = {"null", "string"})
    @Size(max = 15)
    private String shipRegion;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH,
        description = "The shipperId of the shipper used for this order.")
    @Min(1)
    private Integer shipVia;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderDetailsDto {

    @Schema(types = {"number", "string"}, format = "float", pattern = OpenApiConfig.FLOAT_PATTERN,
        minimum = "0", maximum = "1",
        description = "Discount as a fraction of the unit price (0 to 1). Database default is 0.")
    @NotNull
    @DecimalMin("0")
    @DecimalMax("1")
    private Float discount;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @NotNull
    @Min(1)
    private Integer orderId;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @NotNull
    @Min(1)
    private Integer productId;

    @Schema(types = {"integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = "1", maximum = OpenApiConfig.INT16_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT16_MAX_LENGTH,
        description = "Database default is 1.")
    @NotNull
    @Min(1)
    @Max(Short.MAX_VALUE)
    private Integer quantity;

    @Schema(types = {"number", "string"}, format = "double", pattern = OpenApiConfig.DECIMAL_PATTERN,
        minimum = "0", maximum = OpenApiConfig.MONEY_MAXIMUM, description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @NotNull
    @DecimalMin("0")
    @DecimalMax(OpenApiConfig.MONEY_MAXIMUM)
    @Digits(integer = 15, fraction = 4)
    private BigDecimal unitPrice;

    public Float getDiscount() {
        return discount;
    }

    public void setDiscount(Float discount) {
        this.discount = discount;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}

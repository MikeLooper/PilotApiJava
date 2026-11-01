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

    @Schema(types = {"number"}, format = "float",
        minimum = "0", maximum = "1",
        description = "Discount as a fraction of the unit price (0 to 1). Database default is 0.")
    @NotNull
    @DecimalMin("0")
    @DecimalMax("1")
    private BigDecimal discount;

    @Schema(types = {"integer"}, format = "int32",
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM)
    @NotNull
    @Min(1)
    private Integer orderId;

    @Schema(types = {"integer"}, format = "int32",
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM)
    @NotNull
    @Min(1)
    private Integer productId;

    @Schema(types = {"integer"}, format = "int16",
        minimum = "1", maximum = OpenApiConfig.INT16_MAXIMUM,
        description = "Database default is 1.")
    @NotNull
    @Min(1)
    @Max(Short.MAX_VALUE)
    private Integer quantity;

    @Schema(types = {"number"}, format = "double",
        minimum = "0", maximum = OpenApiConfig.MONEY_MAXIMUM, description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @NotNull
    @DecimalMin("0")
    @DecimalMax(OpenApiConfig.MONEY_MAXIMUM)
    @Digits(integer = 15, fraction = 4)
    private BigDecimal unitPrice;

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
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

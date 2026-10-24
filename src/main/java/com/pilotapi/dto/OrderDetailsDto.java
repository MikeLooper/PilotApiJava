package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class OrderDetailsDto {

    @Schema(types = {"number", "string"}, format = "float", pattern = OpenApiConfig.FLOAT_PATTERN)
    @NotNull
    private Float discount;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    @NotNull
    private Integer orderId;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    @NotNull
    private Integer productId;

    @Schema(types = {"integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN)
    @NotNull
    private Integer quantity;

    @Schema(types = {"number", "string"}, format = "double", pattern = OpenApiConfig.DECIMAL_PATTERN)
    @NotNull
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

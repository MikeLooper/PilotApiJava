package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.micrometer.common.lang.Nullable;

public class ProductsDto {

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    private Integer categoryId;
    private boolean discontinued;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    @Nullable
    private Integer productId;

    @Schema(types = {"null", "string"})
    @NotBlank
    private String productName;

    @Schema(types = {"null", "string"})
    private String quantityPerUnit;

    @Schema(types = {"integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull
    private Integer reorderLevel = 0;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    private Integer supplierId;

    @Schema(types = {"null", "number", "string"}, format = "double", pattern = OpenApiConfig.DECIMAL_PATTERN)
    private BigDecimal unitPrice;

    @Schema(types = {"integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull
    private Integer unitsInStock = 0;

    @Schema(types = {"integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @NotNull
    private Integer unitsOnOrder = 0;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(boolean discontinued) {
        this.discontinued = discontinued;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantityPerUnit() {
        return quantityPerUnit;
    }

    public void setQuantityPerUnit(String quantityPerUnit) {
        this.quantityPerUnit = quantityPerUnit;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getUnitsInStock() {
        return unitsInStock;
    }

    public void setUnitsInStock(Integer unitsInStock) {
        this.unitsInStock = unitsInStock;
    }

    public Integer getUnitsOnOrder() {
        return unitsOnOrder;
    }

    public void setUnitsOnOrder(Integer unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
    }
}

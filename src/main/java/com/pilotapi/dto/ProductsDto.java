package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.micrometer.common.lang.Nullable;

public class ProductsDto {

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @Min(1)
    private Integer categoryId;

    @Schema(description = "Database default is false.")
    private boolean discontinued;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @Min(1)
    @Nullable
    private Integer productId;

    @Schema(types = {"string"})
    @NotBlank
    @Size(min = 1, max = 40)
    private String productName;

    @Schema(types = {"null", "string"})
    @Size(max = 20)
    private String quantityPerUnit;

    @Schema(types = {"null", "integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = "0", maximum = OpenApiConfig.INT16_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT16_MAX_LENGTH,
        description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @Min(0)
    @Max(Short.MAX_VALUE)
    private Integer reorderLevel = 0;
    private boolean reorderLevelProvided;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH)
    @Min(1)
    private Integer supplierId;

    @Schema(types = {"null", "number", "string"}, format = "double", pattern = OpenApiConfig.DECIMAL_PATTERN,
        minimum = "0", maximum = OpenApiConfig.MONEY_MAXIMUM, description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @DecimalMin("0")
    @DecimalMax(OpenApiConfig.MONEY_MAXIMUM)
    @Digits(integer = 15, fraction = 4)
    private BigDecimal unitPrice;

    @Schema(types = {"null", "integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = "0", maximum = OpenApiConfig.INT16_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT16_MAX_LENGTH,
        description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @Min(0)
    @Max(Short.MAX_VALUE)
    private Integer unitsInStock = 0;
    private boolean unitsInStockProvided;

    @Schema(types = {"null", "integer", "string"}, format = "int16", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = "0", maximum = OpenApiConfig.INT16_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT16_MAX_LENGTH,
        description = OpenApiConfig.DEFAULT_ZERO_DESCRIPTION)
    @Min(0)
    @Max(Short.MAX_VALUE)
    private Integer unitsOnOrder = 0;
    private boolean unitsOnOrderProvided;

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
        this.reorderLevelProvided = true;
    }

    public boolean wasReorderLevelProvided() {
        return reorderLevelProvided;
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
        this.unitsInStockProvided = true;
    }

    public boolean wereUnitsInStockProvided() {
        return unitsInStockProvided;
    }

    public Integer getUnitsOnOrder() {
        return unitsOnOrder;
    }

    public void setUnitsOnOrder(Integer unitsOnOrder) {
        this.unitsOnOrder = unitsOnOrder;
        this.unitsOnOrderProvided = true;
    }

    public boolean wereUnitsOnOrderProvided() {
        return unitsOnOrderProvided;
    }
}

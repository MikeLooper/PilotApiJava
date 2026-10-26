package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddResponseOrderDetails")
public class AddResponseOrderDetailsDto {

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer orderId;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH, requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer productId;

    public AddResponseOrderDetailsDto() {
    }

    public AddResponseOrderDetailsDto(Integer orderId, Integer productId) {
        this.orderId = orderId;
        this.productId = productId;
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
}

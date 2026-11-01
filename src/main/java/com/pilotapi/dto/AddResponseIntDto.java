package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddResponseInt")
public class AddResponseIntDto {

    @Schema(types = {"integer"}, format = "int32",
        minimum = OpenApiConfig.ID_MINIMUM, maximum = OpenApiConfig.INT32_MAXIMUM,
        requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    public AddResponseIntDto() {
    }

    public AddResponseIntDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
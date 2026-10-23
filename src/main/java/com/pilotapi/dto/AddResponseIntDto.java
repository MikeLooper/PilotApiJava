package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddResponseInt")
public class AddResponseIntDto {

    @Schema(types = {"integer"}, format = "int64", pattern = OpenApiConfig.INTEGER_PATTERN)
    private Long id;

    public AddResponseIntDto() {
    }

    public AddResponseIntDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddResponseString")
public class AddResponseStringDto {

    @Schema(types = {"string"}, minLength = OpenApiConfig.CUSTOMER_ID_LENGTH,
        maxLength = OpenApiConfig.CUSTOMER_ID_LENGTH)
    private String id;

    public AddResponseStringDto() {
    }

    public AddResponseStringDto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

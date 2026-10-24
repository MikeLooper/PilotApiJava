package com.pilotapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddResponseString")
public class AddResponseStringDto {

    @Schema(types = {"string"})
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

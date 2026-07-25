package com.pilotapi.dto;

public class AddResponseIntDto {

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
package com.pilotapi.dto;

public class AddResponseStringDto {

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

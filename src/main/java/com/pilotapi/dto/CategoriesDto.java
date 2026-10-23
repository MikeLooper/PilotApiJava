package com.pilotapi.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;

public class CategoriesDto {

    @Nullable
    private Integer categoryId;

    @NotNull
    private String categoryName;

    private String description;
    private String picture;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}

package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;

public class CategoriesDto {

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN)
    @Nullable
    private Integer categoryId;

    @Schema(types = {"null", "string"})
    @NotNull
    private String categoryName;

    @Schema(types = {"null", "string"})
    private String description;

    @Schema(types = {"null", "string"}, format = "byte")
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

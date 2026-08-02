package com.pilotapi.mapper;

import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.model.Category;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CategoryMapper implements EntityDtoMapper<Category, CategoriesDto> {

    @Override
    public CategoriesDto toDto(Category entity) {
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryID(entity.getCategoryID());
        dto.setCategoryName(entity.getCategoryName());
        dto.setDescription(entity.getDescription());
        dto.setPicture(encodeBinary(entity.getPicture()));
        return dto;
    }

    @Override
    public Category toEntity(CategoriesDto dto) {
        Category entity = new Category();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(CategoriesDto dto, Category entity) {
        // entity.setCategoryID(dto.getCategoryID());
        entity.setCategoryName(dto.getCategoryName());
        entity.setDescription(dto.getDescription());
        entity.setPicture(decodeBinary(dto.getPicture()));
    }

    private String encodeBinary(byte[] value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value);
    }

    private byte[] decodeBinary(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException ex) {
            return value.getBytes(StandardCharsets.UTF_8);
        }
    }
}

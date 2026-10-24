package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.mapper.CategoryMapper;
import com.pilotapi.model.Category;
import com.pilotapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends AbstractCrudService<Category, CategoriesDto, Integer, AddResponseIntDto> {

    public CategoryService(CategoryRepository repository, CategoryMapper mapper) {
        super(repository, mapper, CategoriesDto::getCategoryId, intIdResponse(Category::getCategoryID), "Category");
    }
}

package com.pilotapi.service;

import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.mapper.CategoryMapper;
import com.pilotapi.model.Category;
import com.pilotapi.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends AbstractCrudService<Category, CategoriesDto, Integer> {

    public CategoryService(CategoryRepository repository, CategoryMapper mapper) {
        super(repository, mapper, CategoriesDto::getCategoryID, Category::getCategoryID, "Category");
    }
}

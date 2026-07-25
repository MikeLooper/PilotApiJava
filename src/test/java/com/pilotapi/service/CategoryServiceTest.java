package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.CategoryMapper;
import com.pilotapi.model.Category;
import com.pilotapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private CategoryMapper mapper;

    private CategoryService service;

    @BeforeEach
    void setUp() {
        service = new CategoryService(repository, mapper);
    }

    @Test
    void CategoryService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Category entity = new Category();
        entity.setCategoryID(1);
        CategoriesDto dto = new CategoriesDto();
        dto.setCategoryID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<CategoriesDto> result = service.getAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCategoryID());
    }

    @Test
    void CategoryService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1));
    }

    @Test
    void CategoryService_add_returns_generated_id_Test() {
        // Arrange
        CategoriesDto input = new CategoriesDto();
        input.setCategoryID(1);

        Category mapped = new Category();
        Category saved = new Category();
        saved.setCategoryID(99);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(99L, result.getId());
    }

    @Test
    void CategoryService_update_persists_changes_when_found_Test() {
        // Arrange
        CategoriesDto input = new CategoriesDto();
        input.setCategoryID(7);

        Category current = new Category();
        current.setCategoryID(7);

        when(repository.findById(7)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void CategoryService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(5)).thenReturn(true);

        // Act
        service.delete(5);

        // Assert
        verify(repository).deleteById(5);
    }
}

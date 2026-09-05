package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ProductsDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.ProductMapper;
import com.pilotapi.model.Product;
import com.pilotapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(repository, mapper);
    }

    @Test
    void ProductService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Product entity = new Product();
        entity.setProductID(1);
        ProductsDto dto = new ProductsDto();
        dto.setProductID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<ProductsDto> result = service.getAll(0, 20);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getProductID());
    }

    @Test
    void ProductService_getAll_returns_paged_dtos_when_page_specified_Test() {
        // Arrange
        Product entity = new Product();
        entity.setProductID(1);
        ProductsDto dto = new ProductsDto();
        dto.setProductID(1);

        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<ProductsDto> result = service.getAll(2, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getProductID());
    }

    @Test
    void ProductService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1));
    }

    @Test
    void ProductService_add_returns_generated_id_Test() {
        // Arrange
        ProductsDto input = new ProductsDto();
        input.setProductID(1);

        Product mapped = new Product();
        Product saved = new Product();
        saved.setProductID(55);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(55L, result.getId());
    }

    @Test
    void ProductService_update_persists_changes_when_found_Test() {
        // Arrange
        ProductsDto input = new ProductsDto();
        input.setProductID(1);

        Product current = new Product();
        current.setProductID(1);

        when(repository.findById(1)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void ProductService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(1)).thenReturn(true);

        // Act
        service.delete(1);

        // Assert
        verify(repository).deleteById(1);
    }
}

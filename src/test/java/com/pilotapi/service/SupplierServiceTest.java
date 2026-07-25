package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.SupplierMapper;
import com.pilotapi.model.Supplier;
import com.pilotapi.repository.SupplierRepository;
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
class SupplierServiceTest {

    @Mock
    private SupplierRepository repository;

    @Mock
    private SupplierMapper mapper;

    private SupplierService service;

    @BeforeEach
    void setUp() {
        service = new SupplierService(repository, mapper);
    }

    @Test
    void SupplierService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Supplier entity = new Supplier();
        entity.setSupplierID(1);
        SuppliersDto dto = new SuppliersDto();
        dto.setSupplierID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<SuppliersDto> result = service.getAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getSupplierID());
    }

    @Test
    void SupplierService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1));
    }

    @Test
    void SupplierService_add_returns_generated_id_Test() {
        // Arrange
        SuppliersDto input = new SuppliersDto();
        input.setSupplierID(1);

        Supplier mapped = new Supplier();
        Supplier saved = new Supplier();
        saved.setSupplierID(88);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(88L, result.getId());
    }

    @Test
    void SupplierService_update_persists_changes_when_found_Test() {
        // Arrange
        SuppliersDto input = new SuppliersDto();
        input.setSupplierID(1);

        Supplier current = new Supplier();
        current.setSupplierID(1);

        when(repository.findById(1)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void SupplierService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(1)).thenReturn(true);

        // Act
        service.delete(1);

        // Assert
        verify(repository).deleteById(1);
    }
}

package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ShippersDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.ShipperMapper;
import com.pilotapi.model.Shipper;
import com.pilotapi.repository.ShipperRepository;
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
class ShipperServiceTest {

    @Mock
    private ShipperRepository repository;

    @Mock
    private ShipperMapper mapper;

    private ShipperService service;

    @BeforeEach
    void setUp() {
        service = new ShipperService(repository, mapper);
    }

    @Test
    void ShipperService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Shipper entity = new Shipper();
        entity.setShipperID(1);
        ShippersDto dto = new ShippersDto();
        dto.setShipperID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<ShippersDto> result = service.getAll(0, 20);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getShipperID());
    }

    @Test
    void ShipperService_getAll_returns_paged_dtos_when_page_specified_Test() {
        // Arrange
        Shipper entity = new Shipper();
        entity.setShipperID(1);
        ShippersDto dto = new ShippersDto();
        dto.setShipperID(1);

        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<ShippersDto> result = service.getAll(2, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getShipperID());
    }

    @Test
    void ShipperService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1));
    }

    @Test
    void ShipperService_add_returns_generated_id_Test() {
        // Arrange
        ShippersDto input = new ShippersDto();
        input.setShipperID(1);

        Shipper mapped = new Shipper();
        Shipper saved = new Shipper();
        saved.setShipperID(66);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(66L, result.getId());
    }

    @Test
    void ShipperService_update_persists_changes_when_found_Test() {
        // Arrange
        ShippersDto input = new ShippersDto();
        input.setShipperID(1);

        Shipper current = new Shipper();
        current.setShipperID(1);

        when(repository.findById(1)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void ShipperService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(1)).thenReturn(true);

        // Act
        service.delete(1);

        // Assert
        verify(repository).deleteById(1);
    }
}

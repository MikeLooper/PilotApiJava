package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrdersDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.OrderMapper;
import com.pilotapi.model.Order;
import com.pilotapi.repository.OrderRepository;
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
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    private OrderService service;

    @BeforeEach
    void setUp() {
        service = new OrderService(repository, mapper);
    }

    @Test
    void OrderService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Order entity = new Order();
        entity.setOrderID(10);
        OrdersDto dto = new OrdersDto();
        dto.setOrderID(10);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<OrdersDto> result = service.getAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getOrderID());
    }

    @Test
    void OrderService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(10)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(10));
    }

    @Test
    void OrderService_add_returns_generated_id_Test() {
        // Arrange
        OrdersDto input = new OrdersDto();
        input.setOrderID(10);

        Order mapped = new Order();
        Order saved = new Order();
        saved.setOrderID(77);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(77L, result.getId());
    }

    @Test
    void OrderService_update_persists_changes_when_found_Test() {
        // Arrange
        OrdersDto input = new OrdersDto();
        input.setOrderID(10);

        Order current = new Order();
        current.setOrderID(10);

        when(repository.findById(10)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void OrderService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(10)).thenReturn(true);

        // Act
        service.delete(10);

        // Assert
        verify(repository).deleteById(10);
    }
}

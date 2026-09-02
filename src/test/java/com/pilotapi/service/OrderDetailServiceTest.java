package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.OrderDetailMapper;
import com.pilotapi.model.OrderDetail;
import com.pilotapi.model.OrderDetailId;
import com.pilotapi.repository.OrderDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDetailServiceTest {

    @Mock
    private OrderDetailRepository repository;

    @Mock
    private OrderDetailMapper mapper;

    @InjectMocks
    private OrderDetailService service;

    @Test
    void OrderDetailService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        OrderDetail entity = new OrderDetail();
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderID(10);
        dto.setProductID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<OrderDetailsDto> result = service.getAll(0, 20);

        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getOrderID());
    }

    @Test
    void OrderDetailService_getAll_returns_paged_dtos_when_page_specified_Test() {
        // Arrange
        OrderDetail entity = new OrderDetail();
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderID(10);
        dto.setProductID(1);

        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<OrderDetailsDto> result = service.getAll(2, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getOrderID());
    }

    @Test
    void OrderDetailService_getById_returns_mapped_dto_Test() {
        // Arrange
        OrderDetail entity = new OrderDetail();
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderID(10);
        dto.setProductID(1);

        when(repository.findById(new OrderDetailId(1, 10))).thenReturn(Optional.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        OrderDetailsDto result = service.getById(1, 10);

        // Assert
        assertEquals(1, result.getProductID());
        assertEquals(10, result.getOrderID());
    }

    @Test
    void OrderDetailService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(new OrderDetailId(1, 10))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1, 10));
    }

    @Test
    void OrderDetailService_add_returns_order_id_as_response_Test() {
        // Arrange
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setOrderID(10);
        dto.setProductID(1);

        OrderDetail mapped = new OrderDetail();
        OrderDetail saved = new OrderDetail();
        saved.setId(new OrderDetailId(1, 10));

        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto response = service.add(dto);

        // Assert
        assertEquals(10L, response.getId());
    }

    @Test
    void OrderDetailService_update_throws_resource_not_found_when_missing_Test() {
        // Arrange
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductID(1);
        dto.setOrderID(10);

        when(repository.findById(new OrderDetailId(1, 10))).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void OrderDetailService_update_saves_changes_when_found_Test() {
        // Arrange
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setProductID(1);
        dto.setOrderID(10);

        OrderDetail current = new OrderDetail();
        current.setId(new OrderDetailId(1, 10));

        when(repository.findById(new OrderDetailId(1, 10))).thenReturn(Optional.of(current));

        // Act
        service.update(dto);

        // Assert
        verify(mapper).updateEntityFromDto(dto, current);
        verify(repository).save(current);
    }

    @Test
    void OrderDetailService_delete_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.existsById(new OrderDetailId(1, 10))).thenReturn(false);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1, 10));
    }

    @Test
    void OrderDetailService_delete_removes_entity_when_found_Test() {
        // Arrange
        when(repository.existsById(new OrderDetailId(1, 10))).thenReturn(true);

        // Act
        service.delete(1, 10);

        // Assert
        verify(repository).deleteById(new OrderDetailId(1, 10));
    }
}

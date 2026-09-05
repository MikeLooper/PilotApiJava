package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.CustomerMapper;
import com.pilotapi.model.Customer;
import com.pilotapi.repository.CustomerRepository;
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
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private CustomerMapper mapper;

    private CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService(repository, mapper);
    }

    @Test
    void CustomerService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Customer entity = new Customer();
        entity.setCustomerID("ALFKI");
        CustomersDto dto = new CustomersDto();
        dto.setCustomerID("ALFKI");

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<CustomersDto> result = service.getAll(0, 20);

        // Assert
        assertEquals(1, result.size());
        assertEquals("ALFKI", result.get(0).getCustomerID());
    }

    @Test
    void CustomerService_getAll_returns_paged_dtos_when_page_specified_Test() {
        // Arrange
        Customer entity = new Customer();
        entity.setCustomerID("ALFKI");
        CustomersDto dto = new CustomersDto();
        dto.setCustomerID("ALFKI");

        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<CustomersDto> result = service.getAll(2, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals("ALFKI", result.get(0).getCustomerID());
    }

    @Test
    void CustomerService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById("ALFKI")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById("ALFKI"));
    }

    @Test
    void CustomerService_add_returns_zero_id_for_non_numeric_identifier_Test() {
        // Arrange
        CustomersDto input = new CustomersDto();
        input.setCustomerID("ALFKI");

        Customer mapped = new Customer();
        Customer saved = new Customer();
        saved.setCustomerID("ALFKI");

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(0L, result.getId());
    }

    @Test
    void CustomerService_update_persists_changes_when_found_Test() {
        // Arrange
        CustomersDto input = new CustomersDto();
        input.setCustomerID("ALFKI");

        Customer current = new Customer();
        current.setCustomerID("ALFKI");

        when(repository.findById("ALFKI")).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void CustomerService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById("ALFKI")).thenReturn(true);

        // Act
        service.delete("ALFKI");

        // Assert
        verify(repository).deleteById("ALFKI");
    }
}

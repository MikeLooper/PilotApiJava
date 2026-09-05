package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.EmployeesDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.EmployeeMapper;
import com.pilotapi.model.Employee;
import com.pilotapi.repository.EmployeeRepository;
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
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @Mock
    private EmployeeMapper mapper;

    private EmployeeService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeService(repository, mapper);
    }

    @Test
    void EmployeeService_getAll_returns_mapped_dtos_Test() {
        // Arrange
        Employee entity = new Employee();
        entity.setEmployeeID(1);
        EmployeesDto dto = new EmployeesDto();
        dto.setEmployeeID(1);

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<EmployeesDto> result = service.getAll(0, 20);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEmployeeID());
    }

    @Test
    void EmployeeService_getAll_returns_paged_dtos_when_page_specified_Test() {
        // Arrange
        Employee entity = new Employee();
        entity.setEmployeeID(1);
        EmployeesDto dto = new EmployeesDto();
        dto.setEmployeeID(1);

        when(repository.findAll(PageRequest.of(1, 10))).thenReturn(new PageImpl<>(List.of(entity)));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act
        List<EmployeesDto> result = service.getAll(2, 10);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEmployeeID());
    }

    @Test
    void EmployeeService_getById_throws_resource_not_found_when_missing_Test() {
        // Arrange
        when(repository.findById(1)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.getById(1));
    }

    @Test
    void EmployeeService_add_returns_generated_id_Test() {
        // Arrange
        EmployeesDto input = new EmployeesDto();
        input.setEmployeeID(1);

        Employee mapped = new Employee();
        Employee saved = new Employee();
        saved.setEmployeeID(99);

        when(mapper.toEntity(input)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(saved);

        // Act
        AddResponseIntDto result = service.add(input);

        // Assert
        assertEquals(99L, result.getId());
    }

    @Test
    void EmployeeService_update_persists_changes_when_found_Test() {
        // Arrange
        EmployeesDto input = new EmployeesDto();
        input.setEmployeeID(7);

        Employee current = new Employee();
        current.setEmployeeID(7);

        when(repository.findById(7)).thenReturn(Optional.of(current));

        // Act
        service.update(input);

        // Assert
        verify(mapper).updateEntityFromDto(input, current);
        verify(repository).save(current);
    }

    @Test
    void EmployeeService_delete_removes_entity_when_exists_Test() {
        // Arrange
        when(repository.existsById(5)).thenReturn(true);

        // Act
        service.delete(5);

        // Assert
        verify(repository).deleteById(5);
    }
}

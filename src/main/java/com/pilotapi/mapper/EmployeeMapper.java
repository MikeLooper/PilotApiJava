package com.pilotapi.mapper;

import com.pilotapi.dto.EmployeesDto;
import com.pilotapi.model.Employee;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EmployeeMapper implements EntityDtoMapper<Employee, EmployeesDto> {

    @Override
    public EmployeesDto toDto(Employee entity) {
        EmployeesDto dto = new EmployeesDto();
        dto.setAddress(entity.getAddress());
        dto.setBirthDate(entity.getBirthDate());
        dto.setCity(entity.getCity());
        dto.setCountry(entity.getCountry());
        dto.setEmployeeID(entity.getEmployeeID());
        dto.setExtension(entity.getExtension());
        dto.setFirstName(entity.getFirstName());
        dto.setHireDate(entity.getHireDate());
        dto.setHomePhone(entity.getHomePhone());
        dto.setLastName(entity.getLastName());
        dto.setNotes(entity.getNotes());
        dto.setPhoto(encodeBinary(entity.getPhoto()));
        dto.setPhotoPath(entity.getPhotoPath());
        dto.setPostalCode(entity.getPostalCode());
        dto.setRegion(entity.getRegion());
        dto.setReportsTo(entity.getReportsTo());
        dto.setTitle(entity.getTitle());
        dto.setTitleOfCourtesy(entity.getTitleOfCourtesy());
        return dto;
    }

    @Override
    public Employee toEntity(EmployeesDto dto) {
        Employee entity = new Employee();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(EmployeesDto dto, Employee entity) {
        entity.setAddress(dto.getAddress());
        entity.setBirthDate(dto.getBirthDate());
        entity.setCity(dto.getCity());
        entity.setCountry(dto.getCountry());
        entity.setEmployeeID(dto.getEmployeeID());
        entity.setExtension(dto.getExtension());
        entity.setFirstName(dto.getFirstName());
        entity.setHireDate(dto.getHireDate());
        entity.setHomePhone(dto.getHomePhone());
        entity.setLastName(dto.getLastName());
        entity.setNotes(dto.getNotes());
        entity.setPhoto(decodeBinary(dto.getPhoto()));
        entity.setPhotoPath(dto.getPhotoPath());
        entity.setPostalCode(dto.getPostalCode());
        entity.setRegion(dto.getRegion());
        entity.setReportsTo(dto.getReportsTo());
        entity.setTitle(dto.getTitle());
        entity.setTitleOfCourtesy(dto.getTitleOfCourtesy());
    }

    private String encodeBinary(byte[] value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value);
    }

    private byte[] decodeBinary(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(value);
        } catch (IllegalArgumentException ex) {
            return value.getBytes(StandardCharsets.UTF_8);
        }
    }
}

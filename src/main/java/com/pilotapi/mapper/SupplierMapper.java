package com.pilotapi.mapper;

import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper implements EntityDtoMapper<Supplier, SuppliersDto> {

    @Override
    public SuppliersDto toDto(Supplier entity) {
        SuppliersDto dto = new SuppliersDto();
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setCompanyName(entity.getCompanyName());
        dto.setContactName(entity.getContactName());
        dto.setContactTitle(entity.getContactTitle());
        dto.setCountry(entity.getCountry());
        dto.setFax(entity.getFax());
        dto.setHomePage(entity.getHomePage());
        dto.setPhone(entity.getPhone());
        dto.setPostalCode(entity.getPostalCode());
        dto.setRegion(entity.getRegion());
        dto.setSupplierID(entity.getSupplierID());
        return dto;
    }

    @Override
    public Supplier toEntity(SuppliersDto dto) {
        Supplier entity = new Supplier();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(SuppliersDto dto, Supplier entity) {
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setCompanyName(dto.getCompanyName());
        entity.setContactName(dto.getContactName());
        entity.setContactTitle(dto.getContactTitle());
        entity.setCountry(dto.getCountry());
        entity.setFax(dto.getFax());
        entity.setHomePage(dto.getHomePage());
        entity.setPhone(dto.getPhone());
        entity.setPostalCode(dto.getPostalCode());
        entity.setRegion(dto.getRegion());
        // entity.setSupplierID(dto.getSupplierID());
    }
}

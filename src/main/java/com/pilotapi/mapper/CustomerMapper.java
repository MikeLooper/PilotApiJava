package com.pilotapi.mapper;

import com.pilotapi.dto.CustomersDto;
import com.pilotapi.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements EntityDtoMapper<Customer, CustomersDto> {

    @Override
    public CustomersDto toDto(Customer entity) {
        CustomersDto dto = new CustomersDto();
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setCompanyName(entity.getCompanyName());
        dto.setContactName(entity.getContactName());
        dto.setContactTitle(entity.getContactTitle());
        dto.setCountry(entity.getCountry());
        dto.setCustomerID(entity.getCustomerID());
        dto.setFax(entity.getFax());
        dto.setPhone(entity.getPhone());
        dto.setPostalCode(entity.getPostalCode());
        dto.setRegion(entity.getRegion());
        return dto;
    }

    @Override
    public Customer toEntity(CustomersDto dto) {
        Customer entity = new Customer();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(CustomersDto dto, Customer entity) {
        entity.setAddress(dto.getAddress());
        entity.setCity(dto.getCity());
        entity.setCompanyName(dto.getCompanyName());
        entity.setContactName(dto.getContactName());
        entity.setContactTitle(dto.getContactTitle());
        entity.setCountry(dto.getCountry());
        entity.setCustomerID(dto.getCustomerID());
        entity.setFax(dto.getFax());
        entity.setPhone(dto.getPhone());
        entity.setPostalCode(dto.getPostalCode());
        entity.setRegion(dto.getRegion());
    }
}

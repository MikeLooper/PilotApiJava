package com.pilotapi.mapper;

import com.pilotapi.dto.ShippersDto;
import com.pilotapi.model.Shipper;
import org.springframework.stereotype.Component;

@Component
public class ShipperMapper implements EntityDtoMapper<Shipper, ShippersDto> {

    @Override
    public ShippersDto toDto(Shipper entity) {
        ShippersDto dto = new ShippersDto();
        dto.setCompanyName(entity.getCompanyName());
        dto.setPhone(entity.getPhone());
        dto.setShipperID(entity.getShipperID());
        return dto;
    }

    @Override
    public Shipper toEntity(ShippersDto dto) {
        Shipper entity = new Shipper();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(ShippersDto dto, Shipper entity) {
        entity.setCompanyName(dto.getCompanyName());
        entity.setPhone(dto.getPhone());
        entity.setShipperID(dto.getShipperID());
    }
}

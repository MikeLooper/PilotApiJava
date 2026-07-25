package com.pilotapi.mapper;

import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.model.OrderDetail;
import com.pilotapi.model.OrderDetailId;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper implements EntityDtoMapper<OrderDetail, OrderDetailsDto> {

    @Override
    public OrderDetailsDto toDto(OrderDetail entity) {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setDiscount(entity.getDiscount());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setOrderID(entity.getId().getOrderID());
        dto.setProductID(entity.getId().getProductID());
        return dto;
    }

    @Override
    public OrderDetail toEntity(OrderDetailsDto dto) {
        OrderDetail entity = new OrderDetail();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(OrderDetailsDto dto, OrderDetail entity) {
        entity.setId(new OrderDetailId(dto.getProductID(), dto.getOrderID()));
        entity.setDiscount(dto.getDiscount());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
    }
}

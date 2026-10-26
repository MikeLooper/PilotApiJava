package com.pilotapi.mapper;

import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.model.OrderDetail;
import com.pilotapi.model.OrderDetailId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderDetailMapper implements EntityDtoMapper<OrderDetail, OrderDetailsDto> {

    @Override
    public OrderDetailsDto toDto(OrderDetail entity) {
        OrderDetailsDto dto = new OrderDetailsDto();
        dto.setDiscount(entity.getDiscount() == null ? null : new BigDecimal(entity.getDiscount().toString()));
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setOrderId(entity.getId().getOrderID());
        dto.setProductId(entity.getId().getProductID());
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
        entity.setId(new OrderDetailId(dto.getProductId(), dto.getOrderId()));
        entity.setDiscount(dto.getDiscount() == null ? null : dto.getDiscount().floatValue());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
    }
}

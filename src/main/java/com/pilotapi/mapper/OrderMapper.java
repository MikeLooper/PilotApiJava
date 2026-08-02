package com.pilotapi.mapper;

import com.pilotapi.dto.OrdersDto;
import com.pilotapi.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper implements EntityDtoMapper<Order, OrdersDto> {

    @Override
    public OrdersDto toDto(Order entity) {
        OrdersDto dto = new OrdersDto();
        dto.setCustomerID(entity.getCustomerID());
        dto.setEmployeeID(entity.getEmployeeID());
        dto.setFreight(entity.getFreight());
        dto.setOrderDate(entity.getOrderDate());
        dto.setOrderID(entity.getOrderID());
        dto.setRequiredDate(entity.getRequiredDate());
        dto.setShipAddress(entity.getShipAddress());
        dto.setShipCity(entity.getShipCity());
        dto.setShipCountry(entity.getShipCountry());
        dto.setShipName(entity.getShipName());
        dto.setShippedDate(entity.getShippedDate());
        dto.setShipPostalCode(entity.getShipPostalCode());
        dto.setShipRegion(entity.getShipRegion());
        dto.setShipVia(entity.getShipVia());
        return dto;
    }

    @Override
    public Order toEntity(OrdersDto dto) {
        Order entity = new Order();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(OrdersDto dto, Order entity) {
        entity.setCustomerID(dto.getCustomerID());
        entity.setEmployeeID(dto.getEmployeeID());
        entity.setFreight(dto.getFreight());
        entity.setOrderDate(dto.getOrderDate());
        // entity.setOrderID(dto.getOrderID());
        entity.setRequiredDate(dto.getRequiredDate());
        entity.setShipAddress(dto.getShipAddress());
        entity.setShipCity(dto.getShipCity());
        entity.setShipCountry(dto.getShipCountry());
        entity.setShipName(dto.getShipName());
        entity.setShippedDate(dto.getShippedDate());
        entity.setShipPostalCode(dto.getShipPostalCode());
        entity.setShipRegion(dto.getShipRegion());
        entity.setShipVia(dto.getShipVia());
    }
}

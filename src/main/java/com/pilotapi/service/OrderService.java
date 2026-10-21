package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrdersDto;
import com.pilotapi.mapper.OrderMapper;
import com.pilotapi.model.Order;
import com.pilotapi.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService extends AbstractCrudService<Order, OrdersDto, Integer, AddResponseIntDto> {

    public OrderService(OrderRepository repository, OrderMapper mapper) {
        super(repository, mapper, OrdersDto::getOrderID, intIdResponse(Order::getOrderID), "Order");
    }
}

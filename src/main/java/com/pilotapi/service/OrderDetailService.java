package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.OrderDetailMapper;
import com.pilotapi.model.OrderDetail;
import com.pilotapi.model.OrderDetailId;
import com.pilotapi.repository.OrderDetailRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailService {

    private final OrderDetailRepository repository;
    private final OrderDetailMapper mapper;

    public OrderDetailService(OrderDetailRepository repository, OrderDetailMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<OrderDetailsDto> getAll(int page, int pageSize) {
        if (page == 0) {
            return repository.findAll().stream().map(mapper::toDto).toList();
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return repository.findAll(pageable).stream().map(mapper::toDto).toList();
    }

    public OrderDetailsDto getById(Integer productId, Integer orderId) {
        return mapper.toDto(repository.findById(new OrderDetailId(productId, orderId))
            .orElseThrow(() -> new ResourceNotFoundException("Order detail not found")));
    }

    public AddResponseIntDto add(OrderDetailsDto dto) {
        OrderDetail saved = repository.save(mapper.toEntity(dto));
        return new AddResponseIntDto(saved.getId().getOrderID().longValue());
    }

    public boolean update(OrderDetailsDto dto) {
        OrderDetailId id = new OrderDetailId(dto.getProductID(), dto.getOrderID());
        OrderDetail entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order detail not found"));
        mapper.updateEntityFromDto(dto, entity);
        repository.save(entity);
        return true;
    }

    public boolean delete(Integer productId, Integer orderId) {
        OrderDetailId id = new OrderDetailId(productId, orderId);
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Order detail not found");
        }
        repository.deleteById(id);
        return true;
    }
}

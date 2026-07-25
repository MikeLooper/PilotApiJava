package com.pilotapi.repository;

import com.pilotapi.model.OrderDetail;
import com.pilotapi.model.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}

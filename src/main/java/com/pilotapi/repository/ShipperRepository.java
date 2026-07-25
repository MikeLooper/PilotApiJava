package com.pilotapi.repository;

import com.pilotapi.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipperRepository extends JpaRepository<Shipper, Integer> {
}

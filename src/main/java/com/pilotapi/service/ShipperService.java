package com.pilotapi.service;

import com.pilotapi.dto.ShippersDto;
import com.pilotapi.mapper.ShipperMapper;
import com.pilotapi.model.Shipper;
import com.pilotapi.repository.ShipperRepository;
import org.springframework.stereotype.Service;

@Service
public class ShipperService extends AbstractCrudService<Shipper, ShippersDto, Integer> {

    public ShipperService(ShipperRepository repository, ShipperMapper mapper) {
        super(repository, mapper, ShippersDto::getShipperID, Shipper::getShipperID, "Shipper");
    }
}

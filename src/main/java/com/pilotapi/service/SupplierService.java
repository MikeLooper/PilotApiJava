package com.pilotapi.service;

import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.mapper.SupplierMapper;
import com.pilotapi.model.Supplier;
import com.pilotapi.repository.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class SupplierService extends AbstractCrudService<Supplier, SuppliersDto, Integer> {

    public SupplierService(SupplierRepository repository, SupplierMapper mapper) {
        super(repository, mapper, SuppliersDto::getSupplierID, Supplier::getSupplierID, "Supplier");
    }
}

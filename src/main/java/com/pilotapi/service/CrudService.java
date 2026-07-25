package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;

import java.util.List;

public interface CrudService<D, ID> {

    List<D> getAll();

    D getById(ID id);

    AddResponseIntDto add(D dto);

    void update(D dto);

    void delete(ID id);
}

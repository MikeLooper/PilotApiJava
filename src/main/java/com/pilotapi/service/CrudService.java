package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;

import java.util.List;

public interface CrudService<D, ID> {

    List<D> getAll(int page, int pageSize);

    D getById(ID id);

    AddResponseIntDto add(D dto);

    boolean update(D dto);

    boolean delete(ID id);
}

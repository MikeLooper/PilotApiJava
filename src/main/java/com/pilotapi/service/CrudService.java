package com.pilotapi.service;

import java.util.List;

public interface CrudService<D, ID, R> {

    List<D> getAll(int page, int pageSize);

    D getById(ID id);

    R add(D dto);

    boolean update(D dto);

    boolean delete(ID id);
}

package com.pilotapi.mapper;

public interface EntityDtoMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

    void updateEntityFromDto(D dto, E entity);
}

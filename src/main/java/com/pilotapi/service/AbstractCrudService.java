package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.EntityDtoMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractCrudService<E, D, ID> implements CrudService<D, ID> {

    private final JpaRepository<E, ID> repository;
    private final EntityDtoMapper<E, D> mapper;
    private final Function<D, ID> dtoIdExtractor;
    private final Function<E, Object> entityIdExtractor;
    private final String resourceName;

    protected AbstractCrudService(
        JpaRepository<E, ID> repository,
        EntityDtoMapper<E, D> mapper,
        Function<D, ID> dtoIdExtractor,
        Function<E, Object> entityIdExtractor,
        String resourceName
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.dtoIdExtractor = dtoIdExtractor;
        this.entityIdExtractor = entityIdExtractor;
        this.resourceName = resourceName;
    }

    @Override
    public List<D> getAll(int page, int pageSize) {
        if (page == 0) {
            return repository.findAll().stream().map(mapper::toDto).toList();
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return repository.findAll(pageable).stream().map(mapper::toDto).toList();
    }

    @Override
    public D getById(ID id) {
        E entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(resourceName + " not found"));
        return mapper.toDto(entity);
    }

    @Override
    public AddResponseIntDto add(D dto) {
        E saved = repository.save(mapper.toEntity(dto));
        return new AddResponseIntDto(toLong(entityIdExtractor.apply(saved)));
    }

    @Override
    public boolean update(D dto) {
        ID id = dtoIdExtractor.apply(dto);
        if (id == null) {
            throw new IllegalArgumentException("Request payload id is required");
        }

        E entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(resourceName + " not found"));
        mapper.updateEntityFromDto(dto, entity);
        repository.save(entity);
        return true;
    }

    @Override
    public boolean delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(resourceName + " not found");
        }
        repository.deleteById(id);
        return true;
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }
}

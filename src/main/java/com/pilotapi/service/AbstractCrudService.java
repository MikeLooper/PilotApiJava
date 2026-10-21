package com.pilotapi.service;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.exception.ResourceNotFoundException;
import com.pilotapi.mapper.EntityDtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractCrudService<E, D, ID, R> implements CrudService<D, ID, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCrudService.class);

    private final JpaRepository<E, ID> repository;
    private final EntityDtoMapper<E, D> mapper;
    private final Function<D, ID> dtoIdExtractor;
    private final Function<E, R> addResponseMapper;
    private final String resourceName;

    protected AbstractCrudService(
        JpaRepository<E, ID> repository,
        EntityDtoMapper<E, D> mapper,
        Function<D, ID> dtoIdExtractor,
        Function<E, R> addResponseMapper,
        String resourceName
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.dtoIdExtractor = dtoIdExtractor;
        this.addResponseMapper = addResponseMapper;
        this.resourceName = resourceName;
    }

    @Override
    public List<D> getAll(int page, int pageSize) {
        if (page <= 0) {
            List<D> results = repository.findAll().stream().map(mapper::toDto).toList();
            LOGGER.info("Retrieved {} {} record(s) (unpaged)", results.size(), resourceName);
            return results;
        }

        int effectivePageSize = pageSize > 0 ? pageSize : 20;
        Pageable pageable = PageRequest.of(page - 1, effectivePageSize);
        List<D> results = repository.findAll(pageable).stream().map(mapper::toDto).toList();
        LOGGER.info("Retrieved {} {} record(s) for page {} (page size {})", results.size(), resourceName, page, effectivePageSize);
        return results;
    }

    @Override
    public D getById(ID id) {
        E entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(resourceName + " not found"));
        D result = mapper.toDto(entity);
        LOGGER.info("Retrieved {} record with id {}", resourceName, id);
        return result;
    }

    @Override
    public R add(D dto) {
        E saved = repository.save(mapper.toEntity(dto));
        return addResponseMapper.apply(saved);
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

    protected static <E> Function<E, AddResponseIntDto> intIdResponse(Function<E, Object> entityIdExtractor) {
        return saved -> new AddResponseIntDto(toLong(entityIdExtractor.apply(saved)));
    }

    private static long toLong(Object value) {
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

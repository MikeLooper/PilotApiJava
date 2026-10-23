package com.pilotapi.mapper;

import com.pilotapi.dto.ProductsDto;
import com.pilotapi.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements EntityDtoMapper<Product, ProductsDto> {

    @Override
    public ProductsDto toDto(Product entity) {
        ProductsDto dto = new ProductsDto();
        dto.setCategoryId(entity.getCategoryID());
        dto.setDiscontinued(entity.isDiscontinued());
        dto.setProductId(entity.getProductID());
        dto.setProductName(entity.getProductName());
        dto.setQuantityPerUnit(entity.getQuantityPerUnit());
        dto.setReorderLevel(entity.getReorderLevel());
        dto.setSupplierId(entity.getSupplierID());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setUnitsInStock(entity.getUnitsInStock());
        dto.setUnitsOnOrder(entity.getUnitsOnOrder());
        return dto;
    }

    @Override
    public Product toEntity(ProductsDto dto) {
        Product entity = new Product();
        updateEntityFromDto(dto, entity);
        entity.setReorderLevel(dto.getReorderLevel());
        entity.setUnitsInStock(dto.getUnitsInStock());
        entity.setUnitsOnOrder(dto.getUnitsOnOrder());
        return entity;
    }

    @Override
    public void updateEntityFromDto(ProductsDto dto, Product entity) {
        entity.setCategoryID(dto.getCategoryId());
        entity.setDiscontinued(dto.isDiscontinued());
        // entity.setProductID(dto.getProductId());
        entity.setProductName(dto.getProductName());
        entity.setQuantityPerUnit(dto.getQuantityPerUnit());
        if (dto.wasReorderLevelProvided()) {
            entity.setReorderLevel(dto.getReorderLevel());
        }
        entity.setSupplierID(dto.getSupplierId());
        entity.setUnitPrice(dto.getUnitPrice());
        if (dto.wereUnitsInStockProvided()) {
            entity.setUnitsInStock(dto.getUnitsInStock());
        }
        if (dto.wereUnitsOnOrderProvided()) {
            entity.setUnitsOnOrder(dto.getUnitsOnOrder());
        }
    }
}

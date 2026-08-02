package com.pilotapi.mapper;

import com.pilotapi.dto.ProductsDto;
import com.pilotapi.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper implements EntityDtoMapper<Product, ProductsDto> {

    @Override
    public ProductsDto toDto(Product entity) {
        ProductsDto dto = new ProductsDto();
        dto.setCategoryID(entity.getCategoryID());
        dto.setDiscontinued(entity.isDiscontinued());
        dto.setProductID(entity.getProductID());
        dto.setProductName(entity.getProductName());
        dto.setQuantityPerUnit(entity.getQuantityPerUnit());
        dto.setReorderLevel(entity.getReorderLevel());
        dto.setSupplierID(entity.getSupplierID());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setUnitsInStock(entity.getUnitsInStock());
        dto.setUnitsOnOrder(entity.getUnitsOnOrder());
        return dto;
    }

    @Override
    public Product toEntity(ProductsDto dto) {
        Product entity = new Product();
        updateEntityFromDto(dto, entity);
        return entity;
    }

    @Override
    public void updateEntityFromDto(ProductsDto dto, Product entity) {
        entity.setCategoryID(dto.getCategoryID());
        entity.setDiscontinued(dto.isDiscontinued());
        // entity.setProductID(dto.getProductID());
        entity.setProductName(dto.getProductName());
        entity.setQuantityPerUnit(dto.getQuantityPerUnit());
        entity.setReorderLevel(dto.getReorderLevel());
        entity.setSupplierID(dto.getSupplierID());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setUnitsInStock(dto.getUnitsInStock());
        entity.setUnitsOnOrder(dto.getUnitsOnOrder());
    }
}

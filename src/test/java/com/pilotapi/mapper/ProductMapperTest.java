package com.pilotapi.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pilotapi.dto.ProductsDto;
import com.pilotapi.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductMapperTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductMapper mapper = new ProductMapper();

    @Test
    void ProductMapper_updateEntityFromDto_preserves_omitted_stock_levels_Test() throws Exception {
        ProductsDto dto = objectMapper.readValue("{\"productName\":\"Updated\"}", ProductsDto.class);
        Product product = new Product();
        product.setReorderLevel(3);
        product.setUnitsInStock(12);
        product.setUnitsOnOrder(5);

        mapper.updateEntityFromDto(dto, product);

        assertEquals(3, product.getReorderLevel());
        assertEquals(12, product.getUnitsInStock());
        assertEquals(5, product.getUnitsOnOrder());
    }

    @Test
    void ProductMapper_toEntity_defaults_omitted_stock_levels_to_zero_Test() throws Exception {
        ProductsDto dto = objectMapper.readValue("{\"productName\":\"New\"}", ProductsDto.class);

        Product product = mapper.toEntity(dto);

        assertEquals(0, product.getReorderLevel());
        assertEquals(0, product.getUnitsInStock());
        assertEquals(0, product.getUnitsOnOrder());
    }
}

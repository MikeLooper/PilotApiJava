package com.pilotapi.service;

import com.pilotapi.dto.ProductsDto;
import com.pilotapi.mapper.ProductMapper;
import com.pilotapi.model.Product;
import com.pilotapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends AbstractCrudService<Product, ProductsDto, Integer> {

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        super(repository, mapper, ProductsDto::getProductID, Product::getProductID, "Product");
    }
}

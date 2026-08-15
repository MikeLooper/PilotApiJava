package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ProductsDto;
import com.pilotapi.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<ProductsDto> getAll(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getAll();
    }

    @GetMapping("/get/{productId}")
    public ProductsDto getById(
        @PathVariable Integer productId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getById(productId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody ProductsDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody ProductsDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer productId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.delete(productId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

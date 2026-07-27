package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.service.SupplierService;
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

@RestController
@RequestMapping("/suppliers")
public class SuppliersController {

    private final SupplierService service;

    public SuppliersController(SupplierService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<SuppliersDto> getAll(@RequestHeader(name = "ApiVersion", required = false) String apiVersion) {
        return service.getAll();
    }

    @GetMapping("/get/{supplierId}")
    public SuppliersDto getById(
        @PathVariable Integer supplierId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.getById(supplierId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.update(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{supplierId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer supplierId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.delete(supplierId);
        return ResponseEntity.noContent().build();
    }
}

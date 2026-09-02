package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.service.SupplierService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API Version: 1.0
 */
@Tag(name = "Suppliers", description = "Suppliers that provide products")
@RestController
@RequestMapping("/v1/suppliers")
public class SuppliersController {

    private final SupplierService service;

    public SuppliersController(SupplierService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<SuppliersDto> getAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getAll(page, pageSize);
    }

    @GetMapping("/get/{supplierId}")
    public SuppliersDto getById(
        @PathVariable Integer supplierId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getById(supplierId);
    }

    @PostMapping("/add")
    public ResponseEntity<AddResponseIntDto> add(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{supplierId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer supplierId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.delete(supplierId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

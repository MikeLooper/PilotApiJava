package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.service.CustomerService;
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
@RequestMapping("/customers")
public class CustomersController {

    private final CustomerService service;

    public CustomersController(CustomerService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<CustomersDto> getAll(@RequestHeader(name = "ApiVersion", required = false) String apiVersion) {
        return service.getAll();
    }

    @GetMapping("/get/{customerId}")
    public CustomersDto getById(
        @PathVariable String customerId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.getById(customerId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody CustomersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody CustomersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.update(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<Void> delete(
        @PathVariable String customerId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.delete(customerId);
        return ResponseEntity.noContent().build();
    }
}

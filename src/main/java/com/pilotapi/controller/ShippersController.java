package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ShippersDto;
import com.pilotapi.service.ShipperService;
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
@RequestMapping("/shippers")
public class ShippersController {

    private final ShipperService service;

    public ShippersController(ShipperService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<ShippersDto> getAll(@RequestHeader(name = "ApiVersion", required = false) String apiVersion) {
        return service.getAll();
    }

    @GetMapping("/get/{shipperId}")
    public ShippersDto getById(
        @PathVariable Integer shipperId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.getById(shipperId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody ShippersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody ShippersDto request,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.update(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{shipperId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer shipperId,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        service.delete(shipperId);
        return ResponseEntity.noContent().build();
    }
}

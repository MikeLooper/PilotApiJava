package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.ShippersDto;
import com.pilotapi.service.ShipperService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API Version: 1.0
 */
@Tag(name = "Shippers", description = "Shipping carriers used to deliver orders")
@RestController
@RequestMapping("/v1/shippers")
public class ShippersController {

    private final ShipperService service;

    public ShippersController(ShipperService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<ShippersDto> getAll(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getAll();
    }

    @GetMapping("/get/{shipperId}")
    public ShippersDto getById(
        @PathVariable Integer shipperId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getById(shipperId);
    }

    @PostMapping("/add")
    public ResponseEntity<AddResponseIntDto> add(
        @Valid @RequestBody ShippersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> update(
        @Valid @RequestBody ShippersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/delete/{shipperId}")
    public ResponseEntity<Void> delete(
        @PathVariable Integer shipperId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.delete(shipperId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

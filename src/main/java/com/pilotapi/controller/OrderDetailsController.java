package com.pilotapi.controller;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.OrderDetailsDto;
import com.pilotapi.service.OrderDetailService;
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
@RequestMapping("/order-details")
public class OrderDetailsController {

    private final OrderDetailService service;

    public OrderDetailsController(OrderDetailService service) {
        this.service = service;
    }

    @GetMapping("/get-all")
    public List<OrderDetailsDto> getAll(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getAll();
    }

    @GetMapping("/get/product/{productId}/order/{orderId}")
    public OrderDetailsDto getById(
        @PathVariable Integer productId,
        @PathVariable Integer orderId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.getById(productId, orderId);
    }

    @PostMapping("/add")
    public AddResponseIntDto add(
        @Valid @RequestBody OrderDetailsDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return service.add(request);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> update(
        @Valid @RequestBody OrderDetailsDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        service.update(request);
        return ResponseEntity.ok(Map.of("message", "Order detail updated"));
    }

    @DeleteMapping("/delete/product/{productId}/order/{orderId}")
    public ResponseEntity<Map<String, String>> delete(
        @PathVariable Integer productId,
        @PathVariable Integer orderId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        service.delete(productId, orderId);
        return ResponseEntity.ok(Map.of("message", "Order detail deleted"));
    }
}

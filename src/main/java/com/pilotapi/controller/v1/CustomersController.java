package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseStringDto;
import com.pilotapi.dto.CustomersDto;
import com.pilotapi.service.CustomerService;
import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.UUID;

/**
 * API Version: 1.0
 */
@Tag(name = OpenApiConfig.TAG_CUSTOMERS)
@RestController
@RequestMapping("/v1/customers")
public class CustomersController {

    private final CustomerService service;

    public CustomersController(CustomerService service) {
        this.service = service;
    }

    @Operation(summary = "Gets all DTO objects of the customer table.")
    @GetMapping("/get-all")
    public List<CustomersDto> getAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = "1.0.0") String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.getAll(page, pageSize);
    }

    @Operation(summary = "Gets a customer record by its ID.")
    @GetMapping("/get/{customerId}")
    public CustomersDto getById(
        @PathVariable String customerId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = "1.0.0") String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.getById(customerId);
    }

    @Operation(summary = "Adds a new customer record.")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<AddResponseStringDto> add(
        @Valid @RequestBody CustomersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = "1.0.0") String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    @Operation(summary = "Updates an existing customer record.")
    @ApiResponse(responseCode = "204", description = "No Content",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<Void> update(
        @Valid @RequestBody CustomersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = "1.0.0") String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Deletes a customer record by its ID.")
    @ApiResponse(responseCode = "204", description = "No Content",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DeleteMapping("/delete/{customerId}")
    public ResponseEntity<Void> delete(
        @PathVariable String customerId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = "1.0.0") String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.delete(customerId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

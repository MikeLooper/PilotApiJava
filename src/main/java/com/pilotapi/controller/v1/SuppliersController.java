package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.SuppliersDto;
import com.pilotapi.service.SupplierService;
import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@Tag(name = OpenApiConfig.TAG_SUPPLIERS)
@RestController
@RequestMapping("/v1/suppliers")
public class SuppliersController {

    private final SupplierService service;

    public SuppliersController(SupplierService service) {
        this.service = service;
    }

    @Operation(summary = "Gets all DTO objects from the supplier table.")
    @GetMapping("/get-all")
    public List<SuppliersDto> getAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.getAll(page, pageSize);
    }

    @Operation(summary = "Gets a DTO object of the supplier record by its ID.")
    @GetMapping("/get/{supplierId}")
    public SuppliersDto getById(
        @Parameter(schema = @Schema(types = {"integer", "string"}, format = "int32",
            pattern = OpenApiConfig.INTEGER_PATTERN, minimum = OpenApiConfig.ID_MINIMUM,
            maximum = OpenApiConfig.INT32_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH))
        @Min(1) @PathVariable Integer supplierId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.getById(supplierId);
    }

    @Operation(summary = "Adds a new supplier record.")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<AddResponseIntDto> add(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    @Operation(summary = "Updates an existing supplier record.")
    @ApiResponse(responseCode = "204", description = "No Content",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<Void> update(
        @Valid @RequestBody SuppliersDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Deletes a supplier record by its ID.")
    @ApiResponse(responseCode = "204", description = "No Content",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @DeleteMapping("/delete/{supplierId}")
    public ResponseEntity<Void> delete(
        @Parameter(schema = @Schema(types = {"integer", "string"}, format = "int32",
            pattern = OpenApiConfig.INTEGER_PATTERN, minimum = OpenApiConfig.ID_MINIMUM,
            maximum = OpenApiConfig.INT32_MAXIMUM, minLength = 1, maxLength = OpenApiConfig.INT32_MAX_LENGTH))
        @Min(1) @PathVariable Integer supplierId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.delete(supplierId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

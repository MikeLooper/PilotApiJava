package com.pilotapi.controller.v1;

import com.pilotapi.dto.AddResponseIntDto;
import com.pilotapi.dto.CategoriesDto;
import com.pilotapi.service.CategoryService;
import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = OpenApiConfig.TAG_CATEGORIES)
@RestController
@RequestMapping("/v1/categories")
public class CategoriesController {

    private final CategoryService service;

    public CategoriesController(CategoryService service) {
        this.service = service;
    }

    @Operation(summary = "Gets all DTO objects from the category table.")
    @GetMapping("/get-all")
    public ResponseEntity<List<CategoriesDto>> getAll(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "pageSize", defaultValue = "20") int pageSize,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Caller-Name", required = false) String callerName,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        List<CategoriesDto> results = service.getAll(page, pageSize);
        return results == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    @Operation(summary = "Gets a category record by its ID.")
    @GetMapping("/get/{categoryId}")
    public ResponseEntity<CategoriesDto> getById(
        @Parameter(schema = @Schema(types = {"integer"}, format = "int32", minimum = OpenApiConfig.ID_MINIMUM,
            maximum = OpenApiConfig.INT32_MAXIMUM))
        @Min(1) @PathVariable Integer categoryId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Caller-Name", required = false) String callerName,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        CategoriesDto result = service.getById(categoryId);
        return result == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(result);
    }

    @Operation(summary = "Adds a new DTO object of a category record.")
    @ApiResponse(responseCode = "201", description = "Created")
    @PostMapping(path = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<AddResponseIntDto> add(
        @Valid @RequestBody CategoriesDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Caller-Name", required = false) String callerName,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(request));
    }

    @Operation(summary = "Updates an existing DTO object of a category record.")
    @ApiResponse(responseCode = "204", description = "No Content")
    @PutMapping(path = "/update", consumes = {MediaType.APPLICATION_JSON_VALUE, "application/*+json"})
    public ResponseEntity<Void> update(
        @Valid @RequestBody CategoriesDto request,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Caller-Name", required = false) String callerName,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.update(request)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Deletes a DTO object of a category record by its ID.")
    @ApiResponse(responseCode = "204", description = "No Content")
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> delete(
        @Parameter(schema = @Schema(types = {"integer"}, format = "int32", minimum = OpenApiConfig.ID_MINIMUM,
            maximum = OpenApiConfig.INT32_MAXIMUM))
        @Min(1) @PathVariable Integer categoryId,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion,
        @RequestHeader(name = "X-Caller-Name", required = false) String callerName,
        @RequestHeader(name = "X-Correlation-Id", required = false) UUID correlationId
    ) {
        return service.delete(categoryId)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.badRequest().build();
    }
}

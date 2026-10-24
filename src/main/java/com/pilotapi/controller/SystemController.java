package com.pilotapi.controller;

import com.pilotapi.dto.AboutResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.pilotapi.config.OpenApiConfig;
import com.pilotapi.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = OpenApiConfig.TAG_SYSTEM)
@RestController
@RequestMapping
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Operation(summary = "Return an OK.")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(types = {"string"})))
    @GetMapping("/healthcheck")
    public JsonNode healthcheck(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @Parameter(hidden = true)
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion
    ) {
        return TextNode.valueOf(systemService.healthcheck());
    }

    @Operation(summary = "Returns application metadata and optional configuration details.")
    @GetMapping("/about")
    public AboutResponseDto about(
        @RequestParam(name = "show-details", defaultValue = "false") boolean showDetails,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @Parameter(hidden = true)
        @RequestHeader(name = "X-Api-Version", required = false, defaultValue = OpenApiConfig.API_VERSION_PLACEHOLDER) String apiVersion
    ) {
        return systemService.about(showDetails, apiVersion);
    }
}

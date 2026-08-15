package com.pilotapi.controller;

import com.pilotapi.dto.AboutResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.pilotapi.service.SystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/healthcheck")
    public JsonNode healthcheck(
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return TextNode.valueOf(systemService.healthcheck());
    }

    @GetMapping("/about")
    public AboutResponseDto about(
        @RequestParam(name = "show-details", defaultValue = "false") boolean showDetails,
        @RequestHeader(name = "Accept", required = false, defaultValue = "application/json") String accept,
        @RequestHeader(name = "ApiVersion", required = false, defaultValue = "1.0.0") String apiVersion
    ) {
        return systemService.about(showDetails, apiVersion);
    }
}

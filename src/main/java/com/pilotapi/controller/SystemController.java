package com.pilotapi.controller;

import com.pilotapi.dto.AboutResponseDto;
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
    public String healthcheck(@RequestHeader(name = "ApiVersion", required = false) String apiVersion) {
        return systemService.healthcheck();
    }

    @GetMapping("/about")
    public AboutResponseDto about(
        @RequestParam(name = "show-details", defaultValue = "false") boolean showDetails,
        @RequestHeader(name = "ApiVersion", required = false) String apiVersion
    ) {
        return systemService.about(showDetails, apiVersion);
    }
}

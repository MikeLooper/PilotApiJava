package com.pilotapi.service;

import com.pilotapi.config.ApplicationMetadataProperties;
import com.pilotapi.dto.AboutResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SystemService {

    private final ApplicationMetadataProperties metadata;
    private final Environment environment;

    @Value("${spring.application.name:PilotApiJava}")
    private String applicationName;

    public SystemService(ApplicationMetadataProperties metadata, Environment environment) {
        this.metadata = metadata;
        this.environment = environment;
    }

    public String healthcheck() {
        return "OK";
    }

    public AboutResponseDto about(boolean showDetails, String apiVersionHeader) {
        AboutResponseDto response = new AboutResponseDto();
        response.setApiVersion(apiVersionHeader);
        response.setName(metadata.getName() == null ? applicationName : metadata.getName());
        response.setBuildVersion(metadata.getBuildVersion());
        response.setDeployDate(metadata.getDeployDate());

        if (showDetails) {
            Map<String, Object> config = new LinkedHashMap<>();
            config.put("activeProfiles", environment.getActiveProfiles());
            response.setApplicationConfiguration(config);
        }

        return response;
    }
}

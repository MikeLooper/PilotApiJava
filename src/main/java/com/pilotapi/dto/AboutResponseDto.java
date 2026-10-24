package com.pilotapi.dto;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AboutResponse")
public class AboutResponseDto {

    @Schema(types = {"null", "string"})
    private String apiVersion;
    @Nullable
    private ApplicationConfigurationDto applicationConfiguration;

    @Schema(types = {"null", "string"})
    private String buildVersion;

    @Schema(types = {"null", "string"})
    private String deployDate;

    @Schema(types = {"null", "string"})
    private String name;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public ApplicationConfigurationDto getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfigurationDto applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getDeployDate() {
        return deployDate;
    }

    public void setDeployDate(String deployDate) {
        this.deployDate = deployDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
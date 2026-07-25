package com.pilotapi.dto;

public class AboutResponseDto {

    private String apiVersion;
    private Object applicationConfiguration;
    private String buildVersion;
    private String deployDate;
    private String name;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Object getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(Object applicationConfiguration) {
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
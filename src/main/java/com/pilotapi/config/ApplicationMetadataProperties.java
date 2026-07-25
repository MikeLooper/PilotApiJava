package com.pilotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.metadata")
public class ApplicationMetadataProperties {

    private String name;
    private String buildVersion;
    private String deployDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
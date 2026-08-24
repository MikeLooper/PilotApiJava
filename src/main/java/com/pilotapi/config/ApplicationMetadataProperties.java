package com.pilotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.metadata")
public class ApplicationMetadataProperties implements EnvironmentAware {

    private static final String APP_DEPLOY_DATE_ENVIRONMENT_VARIABLE = "APP_DEPLOY_DATE";

    private String name;
    private String buildVersion;
    private String deployDate;

    @Override
    public void setEnvironment(Environment environment) {
        String appDeployDate = environment.getProperty(APP_DEPLOY_DATE_ENVIRONMENT_VARIABLE);
        if (StringUtils.hasText(appDeployDate)) {
            deployDate = appDeployDate;
        }
    }

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
package com.pilotapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationMetadataPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class);

    @Test
    void ApplicationMetadataProperties_deployDate_uses_appDeployDate_environment_value_Test() {
        // Arrange
        String deployDate = "2026-08-23";

        // Act
        contextRunner
                .withPropertyValues("APP_DEPLOY_DATE=" + deployDate)
                .run(context -> {
                    ApplicationMetadataProperties properties = context.getBean(ApplicationMetadataProperties.class);

                    // Assert
                    assertEquals(deployDate, properties.getDeployDate());
                });
    }

    @Test
    void ApplicationMetadataProperties_deployDate_uses_metadata_property_when_provided_Test() {
        // Arrange
        String deployDate = "2026-08-24";

        // Act
        contextRunner
                .withPropertyValues(
                        "APP_DEPLOY_DATE=2026-08-23",
                        "app.metadata.deploy-date=" + deployDate)
                .run(context -> {
                    ApplicationMetadataProperties properties = context.getBean(ApplicationMetadataProperties.class);

                    // Assert
                    assertEquals(deployDate, properties.getDeployDate());
                });
    }

    @Configuration
    @EnableConfigurationProperties(ApplicationMetadataProperties.class)
    static class TestConfiguration {
    }
}
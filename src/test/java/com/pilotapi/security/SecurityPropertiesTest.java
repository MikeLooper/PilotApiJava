package com.pilotapi.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SecurityPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(TestConfiguration.class);

    @Test
    void SecurityProperties_binds_values_from_configuration_Test() {
        // Act
        contextRunner
            .withPropertyValues(
                "app.security.active=false",
                "app.security.provider-url=http://localhost:55001",
                "app.security.realm=local-realm",
                "app.security.client-id=local-client",
                "app.security.resource-area=account")
            .run(context -> {
                SecurityProperties properties = context.getBean(SecurityProperties.class);

                // Assert
                assertFalse(properties.isActive());
                assertEquals("http://localhost:55001", properties.getProviderUrl());
                assertEquals("local-realm", properties.getRealm());
                assertEquals("local-client", properties.getClientId());
                assertEquals("account", properties.getResourceArea());
            });
    }

    @Test
    void SecurityProperties_issuerUri_combines_providerUrl_and_realm_Test() {
        // Act
        contextRunner
            .withPropertyValues(
                "app.security.provider-url=http://localhost:55001",
                "app.security.realm=local-realm")
            .run(context -> {
                SecurityProperties properties = context.getBean(SecurityProperties.class);

                // Assert
                assertEquals("http://localhost:55001/realms/local-realm", properties.issuerUri());
            });
    }

    @Test
    void SecurityProperties_jwkSetUri_appends_certs_path_to_issuerUri_Test() {
        // Act
        contextRunner
            .withPropertyValues(
                "app.security.provider-url=http://localhost:55001",
                "app.security.realm=local-realm")
            .run(context -> {
                SecurityProperties properties = context.getBean(SecurityProperties.class);

                // Assert
                assertEquals(
                    "http://localhost:55001/realms/local-realm/protocol/openid-connect/certs",
                    properties.jwkSetUri());
            });
    }

    @Configuration
    @EnableConfigurationProperties(SecurityProperties.class)
    static class TestConfiguration {
    }
}

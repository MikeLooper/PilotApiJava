package com.pilotapi.service;

import com.pilotapi.config.OpenApiConfig;
import com.pilotapi.config.OpenApiProperties;
import com.pilotapi.dto.ApplicationConfigurationDto;
import com.pilotapi.dto.DataConnectionConfigurationDto;
import com.pilotapi.dto.DataSourceConfigurationDto;
import com.pilotapi.dto.DataSourceTypes;
import com.pilotapi.security.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationConfigurationServiceTest {

    private MockEnvironment environment;
    private OpenApiProperties openApiProperties;
    private SecurityProperties securityProperties;
    private ApplicationConfigurationService service;

    @BeforeEach
    void setUp() {
        environment = new MockEnvironment();
        openApiProperties = new OpenApiProperties();
        securityProperties = new SecurityProperties();
        service = new ApplicationConfigurationService(environment, openApiProperties, securityProperties);
    }

    @Test
    void ApplicationConfigurationService_describe_parses_sql_server_url_Test() {
        // Arrange
        environment.setProperty("spring.datasource.url",
            "jdbc:sqlserver://local_mssql:1433;databaseName=NorthWind;encrypt=true;connectTimeout=30");
        environment.setProperty("spring.jpa.properties.hibernate.default_schema", "dbo");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        DataConnectionConfigurationDto connection = result.getDataConnections().get(0);
        assertEquals("local_mssql", connection.getHost());
        assertEquals(1433, connection.getPort());
        assertEquals(30, connection.getConnectTimeout());
        assertEquals("NorthWind", connection.getDataSourceName());
        DataSourceConfigurationDto dataSource = result.getDataSources().get(0);
        assertEquals("NorthWind", dataSource.getDataSource());
        assertEquals(DataSourceTypes.SQL_SERVER, dataSource.getDataSourceEnum());
        assertEquals("SqlServer", dataSource.getDataSourceType());
        assertEquals("dbo", dataSource.getSchema());
    }

    @Test
    void ApplicationConfigurationService_describe_parses_postgresql_url_Test() {
        // Arrange
        environment.setProperty("spring.datasource.url",
            "jdbc:postgresql://local-postgres:5432/northwind?connectTimeout=10");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        DataConnectionConfigurationDto connection = result.getDataConnections().get(0);
        assertEquals("local-postgres", connection.getHost());
        assertEquals(5432, connection.getPort());
        assertEquals(10, connection.getConnectTimeout());
        DataSourceConfigurationDto dataSource = result.getDataSources().get(0);
        assertEquals("northwind", dataSource.getDataSource());
        assertEquals(DataSourceTypes.POSTGRESQL, dataSource.getDataSourceEnum());
        assertEquals("PostgreSQL", dataSource.getDataSourceType());
        assertNull(dataSource.getSchema());
    }

    @Test
    void ApplicationConfigurationService_describe_reports_unrecognized_data_source_Test() {
        // Arrange
        environment.setProperty("spring.datasource.url", "jdbc:h2:mem:test");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        DataSourceConfigurationDto dataSource = result.getDataSources().get(0);
        assertEquals(DataSourceTypes.UNRECOGNIZED, dataSource.getDataSourceEnum());
        assertNull(dataSource.getDataSourceType());
        assertNull(result.getDataConnections().get(0).getHost());
        assertEquals(0, result.getDataConnections().get(0).getConnectTimeout());
    }

    @Test
    void ApplicationConfigurationService_describe_reports_security_settings_Test() {
        // Arrange
        securityProperties.setActive(false);
        securityProperties.setProviderUrl("http://local-keycloak:8080");
        securityProperties.setRealm("local-realm");
        securityProperties.setClientId("local-client");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        assertFalse(result.getSecurity().isActive());
        assertEquals("http://local-keycloak:8080", result.getSecurity().getBaseUrl());
        assertEquals("local-realm", result.getSecurity().getRealm());
        assertEquals("local-client", result.getSecurity().getClientId());
        assertEquals("http://local-keycloak:8080/realms/local-realm", result.getSecurity().getAuthority());
        assertEquals(ApplicationConfigurationService.CLOCK_SKEW_SECONDS, result.getSecurity().getClockSkewSeconds());
    }

    @Test
    void ApplicationConfigurationService_describe_reports_open_api_settings_Test() {
        // Arrange
        openApiProperties.setTitle("PilotApiJava");
        openApiProperties.setLicense("MIT");
        openApiProperties.getContact().setEmail("someone@example.com");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        assertEquals("PilotApiJava", result.getOpenApi().getTitle());
        assertEquals("MIT", result.getOpenApi().getLicense());
        assertEquals(OpenApiConfig.API_VERSION, result.getOpenApi().getVersion());
        assertEquals("someone@example.com", result.getOpenApi().getContact().getEmail());
    }

    @Test
    void ApplicationConfigurationService_describe_reports_open_telemetry_endpoint_Test() {
        // Arrange
        environment.setProperty("otel.exporter.otlp.endpoint", "http://otel-collector:4317");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        assertTrue(result.getOpenTelemetry().isActive());
        assertEquals("otel-collector", result.getOpenTelemetry().getServer());
        assertEquals(4317, result.getOpenTelemetry().getPort());
    }

    @Test
    void ApplicationConfigurationService_describe_reports_open_telemetry_disabled_Test() {
        // Arrange
        environment.setProperty("otel.sdk.disabled", "true");

        // Act
        ApplicationConfigurationDto result = service.describe();

        // Assert
        assertFalse(result.getOpenTelemetry().isActive());
        assertNull(result.getOpenTelemetry().getServer());
        assertNull(result.getOpenTelemetry().getPort());
    }
}

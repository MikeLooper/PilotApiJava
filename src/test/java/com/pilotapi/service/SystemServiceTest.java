package com.pilotapi.service;

import com.pilotapi.config.ApplicationMetadataProperties;
import com.pilotapi.dto.AboutResponseDto;
import com.pilotapi.dto.ApplicationConfigurationDto;
import com.pilotapi.dto.DataConnectionConfigurationDto;
import com.pilotapi.dto.DataSourceConfigurationDto;
import com.pilotapi.dto.OpenTelemetryConfigurationDto;
import com.pilotapi.dto.SecurityConfigurationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SystemServiceTest {

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private DatabaseMetaData databaseMetaData;

    private ApplicationMetadataProperties metadata;
    private SystemService service;

    @BeforeEach
    void setUp() throws SQLException {
        metadata = new ApplicationMetadataProperties();
        lenient().when(dataSource.getConnection()).thenReturn(connection);
        lenient().when(connection.getMetaData()).thenReturn(databaseMetaData);
        lenient().when(databaseMetaData.getDatabaseProductName()).thenReturn("Microsoft SQL Server");
        service = new SystemService(metadata, dataSource, applicationConfigurationService);
        ReflectionTestUtils.setField(service, "applicationName", "PilotApiJava");
    }

    @Test
    void SystemService_healthcheck_returns_ok_Test() {
        // Arrange

        // Act
        String result = service.healthcheck();

        // Assert
        assertEquals("OK", result);
    }

    @Test
    void SystemService_about_uses_metadata_name_when_provided_Test() {
        // Arrange
        metadata.setName("ConfiguredName");
        metadata.setBuildVersion("1.2.3");
        metadata.setDeployDate("2026-07-24");

        // Act
        AboutResponseDto result = service.about(false, "v1");

        // Assert
        assertEquals("v1", result.getApiVersion());
        assertEquals("ConfiguredName (SQL Server)", result.getName());
        assertEquals("1.2.3", result.getBuildVersion());
        assertEquals("2026-07-24", result.getDeployDate());
        assertNull(result.getApplicationConfiguration());
    }

    @Test
    void SystemService_about_uses_application_name_fallback_and_includes_details_Test() {
        // Arrange
        ApplicationConfigurationDto configuration = new ApplicationConfigurationDto();
        DataConnectionConfigurationDto dataConnection = new DataConnectionConfigurationDto();
        dataConnection.setHost("database.internal");
        dataConnection.setPort(5432);
        dataConnection.setDataSourceName("production");
        configuration.setDataConnections(List.of(dataConnection));
        DataSourceConfigurationDto dataSourceConfiguration = new DataSourceConfigurationDto();
        dataSourceConfiguration.setDataSource("production");
        dataSourceConfiguration.setDataSourceName("production");
        dataSourceConfiguration.setSchema("private");
        configuration.setDataSources(List.of(dataSourceConfiguration));
        SecurityConfigurationDto security = new SecurityConfigurationDto();
        security.setBaseUrl("https://identity.internal");
        security.setRealm("internal");
        security.setClientId("pilot-api");
        security.setAuthority("https://identity.internal/internal");
        configuration.setSecurity(security);
        OpenTelemetryConfigurationDto openTelemetry = new OpenTelemetryConfigurationDto();
        openTelemetry.setServer("telemetry.internal");
        openTelemetry.setPort(4317);
        configuration.setOpenTelemetry(openTelemetry);
        when(applicationConfigurationService.describe()).thenReturn(configuration);

        // Act
        AboutResponseDto result = service.about(true, "v2");

        // Assert
        assertEquals("v2", result.getApiVersion());
        assertEquals("PilotApiJava (SQL Server)", result.getName());
        assertSame(configuration, result.getApplicationConfiguration());
        assertNull(configuration.getDataConnections());
        assertNull(configuration.getDataSources());
        assertNull(security.getBaseUrl());
        assertNull(security.getRealm());
        assertNull(security.getClientId());
        assertNull(security.getAuthority());
        assertNull(openTelemetry.getServer());
        assertNull(openTelemetry.getPort());
    }

    @Test
    void SystemService_about_appends_postgresql_when_connected_to_postgres_Test() throws SQLException {
        // Arrange
        when(databaseMetaData.getDatabaseProductName()).thenReturn("PostgreSQL");

        // Act
        AboutResponseDto result = service.about(false, "v1");

        // Assert
        assertEquals("PilotApiJava (PostgreSQL)", result.getName());
    }

    @Test
    void SystemService_about_omits_database_suffix_when_connection_unavailable_Test() throws SQLException {
        // Arrange
        when(dataSource.getConnection()).thenThrow(new SQLException("connection refused"));

        // Act
        AboutResponseDto result = service.about(false, "v1");

        // Assert
        assertEquals("PilotApiJava", result.getName());
    }
}

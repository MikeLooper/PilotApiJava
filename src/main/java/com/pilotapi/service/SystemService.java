package com.pilotapi.service;

import com.pilotapi.config.ApplicationMetadataProperties;
import com.pilotapi.dto.AboutResponseDto;
import com.pilotapi.dto.ApplicationConfigurationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

@Service
public class SystemService {

    private final ApplicationMetadataProperties metadata;
    private final DataSource dataSource;
    private final ApplicationConfigurationService applicationConfigurationService;

    @Value("${spring.application.name:PilotApiJava}")
    private String applicationName;

    public SystemService(ApplicationMetadataProperties metadata, DataSource dataSource,
                         ApplicationConfigurationService applicationConfigurationService) {
        this.metadata = metadata;
        this.dataSource = dataSource;
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public String healthcheck() {
        return "OK";
    }

    public AboutResponseDto about(boolean showDetails, String apiVersionHeader) {
        AboutResponseDto response = new AboutResponseDto();
        response.setApiVersion(apiVersionHeader);
        response.setName(buildNameWithDatabase(metadata.getName() == null ? applicationName : metadata.getName()));
        response.setBuildVersion(metadata.getBuildVersion());
        response.setDeployDate(metadata.getDeployDate());

        if (showDetails) {
            response.setApplicationConfiguration(redactInfrastructureDetails(applicationConfigurationService.describe()));
        }

        return response;
    }

    private static ApplicationConfigurationDto redactInfrastructureDetails(ApplicationConfigurationDto configuration) {
        if (configuration == null) {
            return null;
        }

        configuration.setDataConnections(null);
        configuration.setDataSources(null);

        if (configuration.getSecurity() != null) {
            configuration.getSecurity().setBaseUrl(null);
            configuration.getSecurity().setRealm(null);
            configuration.getSecurity().setClientId(null);
            configuration.getSecurity().setAuthority(null);
        }

        if (configuration.getOpenTelemetry() != null) {
            configuration.getOpenTelemetry().setServer(null);
            configuration.getOpenTelemetry().setPort(null);
        }

        return configuration;
    }

    private String buildNameWithDatabase(String name) {
        String databaseName = resolveDatabaseName();
        return databaseName == null ? name : name + " (" + databaseName + ")";
    }

    private String resolveDatabaseName() {
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName();
            if (productName == null) {
                return null;
            }

            String normalized = productName.toLowerCase(Locale.ROOT);
            if (normalized.contains("sql server")) {
                return "SQL Server";
            }
            if (normalized.contains("postgresql")) {
                return "PostgreSQL";
            }
            return productName;
        } catch (SQLException e) {
            return null;
        }
    }
}

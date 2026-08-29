package com.pilotapi.service;

import com.pilotapi.config.ApplicationMetadataProperties;
import com.pilotapi.dto.AboutResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class SystemService {

    private final ApplicationMetadataProperties metadata;
    private final Environment environment;
    private final DataSource dataSource;

    @Value("${spring.application.name:PilotApiJava}")
    private String applicationName;

    public SystemService(ApplicationMetadataProperties metadata, Environment environment, DataSource dataSource) {
        this.metadata = metadata;
        this.environment = environment;
        this.dataSource = dataSource;
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
            Map<String, Object> config = new LinkedHashMap<>();
            config.put("activeProfiles", environment.getActiveProfiles());
            response.setApplicationConfiguration(config);
        }

        return response;
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

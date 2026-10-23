package com.pilotapi.service;

import com.pilotapi.config.OpenApiConfig;
import com.pilotapi.config.OpenApiProperties;
import com.pilotapi.dto.ApplicationConfigurationDto;
import com.pilotapi.dto.DataConnectionConfigurationDto;
import com.pilotapi.dto.DataSourceConfigurationDto;
import com.pilotapi.dto.DataSourceTypes;
import com.pilotapi.dto.OpenApiConfigurationDto;
import com.pilotapi.dto.OpenApiContactConfigurationDto;
import com.pilotapi.dto.OpenTelemetryConfigurationDto;
import com.pilotapi.dto.SecurityConfigurationDto;
import com.pilotapi.security.SecurityProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Describes the running application's configuration for {@code /about?show-details=true}.
 * Only non-sensitive values are reported: credentials are never included.
 */
@Service
public class ApplicationConfigurationService {

    /** Spring Security's default JWT timestamp clock skew ({@code JwtTimestampValidator}). */
    static final int CLOCK_SKEW_SECONDS = 60;

    private static final String SQL_SERVER_PREFIX = "jdbc:sqlserver://";
    private static final String POSTGRESQL_PREFIX = "jdbc:postgresql://";

    private final Environment environment;
    private final OpenApiProperties openApiProperties;
    private final SecurityProperties securityProperties;

    public ApplicationConfigurationService(Environment environment, OpenApiProperties openApiProperties,
                                           SecurityProperties securityProperties) {
        this.environment = environment;
        this.openApiProperties = openApiProperties;
        this.securityProperties = securityProperties;
    }

    public ApplicationConfigurationDto describe() {
        JdbcUrl jdbcUrl = JdbcUrl.parse(environment.getProperty("spring.datasource.url"));

        ApplicationConfigurationDto configuration = new ApplicationConfigurationDto();
        configuration.setDataConnections(List.of(dataConnection(jdbcUrl)));
        configuration.setDataSources(List.of(dataSource(jdbcUrl)));
        configuration.setSecurity(security());
        configuration.setOpenApi(openApi());
        configuration.setOpenTelemetry(openTelemetry());
        return configuration;
    }

    private static DataConnectionConfigurationDto dataConnection(JdbcUrl jdbcUrl) {
        DataConnectionConfigurationDto dataConnection = new DataConnectionConfigurationDto();
        dataConnection.setConnectTimeout(jdbcUrl.connectTimeout());
        return dataConnection;
    }

    private DataSourceConfigurationDto dataSource(JdbcUrl jdbcUrl) {
        DataSourceConfigurationDto dataSource = new DataSourceConfigurationDto();
        dataSource.setDataSourceEnum(jdbcUrl.type());
        dataSource.setDataSourceType(switch (jdbcUrl.type()) {
            case SQL_SERVER -> "SqlServer";
            case POSTGRESQL -> "PostgreSQL";
            case UNRECOGNIZED -> null;
        });
        String schema = environment.getProperty("spring.jpa.properties.hibernate.default_schema");
        dataSource.setSchema(StringUtils.hasText(schema) ? schema : null);
        return dataSource;
    }

    private SecurityConfigurationDto security() {
        SecurityConfigurationDto security = new SecurityConfigurationDto();
        security.setActive(securityProperties.isActive());
        // Signing keys are fetched from the JWK-set URI as configured; HTTPS is not enforced.
        security.setRequireHttpsMetadata(false);
        security.setClockSkewSeconds(CLOCK_SKEW_SECONDS);
        return security;
    }

    private OpenApiConfigurationDto openApi() {
        OpenApiContactConfigurationDto contact = new OpenApiContactConfigurationDto();
        contact.setEmail(openApiProperties.getContact().getEmail());
        contact.setName(openApiProperties.getContact().getName());
        contact.setUrl(openApiProperties.getContact().getUrl());

        OpenApiConfigurationDto openApi = new OpenApiConfigurationDto();
        openApi.setContact(contact);
        openApi.setDescription(openApiProperties.getDescription());
        openApi.setLicense(openApiProperties.getLicense());
        openApi.setSummary(openApiProperties.getSummary());
        openApi.setTitle(openApiProperties.getTitle());
        openApi.setVersion(OpenApiConfig.API_VERSION);
        return openApi;
    }

    private OpenTelemetryConfigurationDto openTelemetry() {
        OpenTelemetryConfigurationDto openTelemetry = new OpenTelemetryConfigurationDto();
        openTelemetry.setActive(!environment.getProperty("otel.sdk.disabled", Boolean.class, false));
        return openTelemetry;
    }

    /** The parts of a SQL Server or PostgreSQL JDBC URL reported by {@code /about}. */
    record JdbcUrl(DataSourceTypes type, String host, Integer port, String database, int connectTimeout) {

        private static final JdbcUrl UNRECOGNIZED = new JdbcUrl(DataSourceTypes.UNRECOGNIZED, null, null, null, 0);

        static JdbcUrl parse(String url) {
            if (url == null) {
                return UNRECOGNIZED;
            }
            if (url.startsWith(SQL_SERVER_PREFIX)) {
                return parseSqlServer(url.substring(SQL_SERVER_PREFIX.length()));
            }
            if (url.startsWith(POSTGRESQL_PREFIX)) {
                return parsePostgreSql(url.substring("jdbc:".length()));
            }
            return UNRECOGNIZED;
        }

        /** {@code jdbc:sqlserver://host[:port][;property=value]...} */
        private static JdbcUrl parseSqlServer(String remainder) {
            String[] segments = remainder.split(";");
            Map<String, String> properties = new LinkedHashMap<>();
            for (int i = 1; i < segments.length; i++) {
                int separator = segments[i].indexOf('=');
                if (separator > 0) {
                    properties.put(segments[i].substring(0, separator).trim().toLowerCase(Locale.ROOT),
                        segments[i].substring(separator + 1).trim());
                }
            }
            String hostAndPort = segments[0];
            int portSeparator = hostAndPort.lastIndexOf(':');
            String host = portSeparator < 0 ? hostAndPort : hostAndPort.substring(0, portSeparator);
            Integer port = portSeparator < 0 ? null : parseInteger(hostAndPort.substring(portSeparator + 1));
            String database = properties.getOrDefault("databasename", properties.get("database"));
            Integer timeout = parseInteger(properties.getOrDefault("connecttimeout", properties.get("logintimeout")));
            return new JdbcUrl(DataSourceTypes.SQL_SERVER, emptyToNull(host), port, database,
                timeout == null ? 0 : timeout);
        }

        /** {@code jdbc:postgresql://host[:port]/database[?property=value&...]} */
        private static JdbcUrl parsePostgreSql(String url) {
            URI uri = URI.create(url);
            String path = uri.getPath();
            String database = path == null || path.length() <= 1 ? null : path.substring(1);
            Integer timeout = null;
            if (uri.getQuery() != null) {
                for (String pair : uri.getQuery().split("&")) {
                    if (pair.startsWith("connectTimeout=")) {
                        timeout = parseInteger(pair.substring("connectTimeout=".length()));
                    }
                }
            }
            return new JdbcUrl(DataSourceTypes.POSTGRESQL, uri.getHost(), uri.getPort() < 0 ? null : uri.getPort(),
                database, timeout == null ? 0 : timeout);
        }

        private static Integer parseInteger(String value) {
            try {
                return value == null ? null : Integer.valueOf(value.trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        private static String emptyToNull(String value) {
            return StringUtils.hasText(value) ? value : null;
        }
    }
}

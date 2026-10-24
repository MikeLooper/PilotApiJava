package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes the OpenTelemetry collector telemetry is exported to.
 */
@Schema(name = "OpenTelemetryConfiguration", description = ConfigurationBaseDto.BASE_DESCRIPTION)
public class OpenTelemetryConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        description = "Gets or sets the OpenTelemetry Port.")
    private Integer port;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenTelemetry Server.")
    private String server;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}

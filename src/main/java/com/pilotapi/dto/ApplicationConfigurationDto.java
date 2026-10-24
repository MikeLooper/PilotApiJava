package com.pilotapi.dto;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * The non-sensitive application configuration returned by {@code /about?show-details=true}.
 */
@Schema(name = "IApplicationConfiguration", description = "Configuration for the application.")
public class ApplicationConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"null", "array"},
        description = "Gets or sets the list of data connection configurations for the application.")
    private List<DataConnectionConfigurationDto> dataConnections;

    @Schema(types = {"null", "array"},
        description = "Gets or sets the list of data source configurations for the application.")
    private List<DataSourceConfigurationDto> dataSources;

    @Schema(description = "Gets or sets an object with Security settings.")
    @Nullable
    private SecurityConfigurationDto security;

    @Schema(description = "Gets or sets an object with OpenApi settings.")
    @Nullable
    private OpenApiConfigurationDto openApi;

    @Schema(description = "Gets or sets an object with Open Telemetry settings.")
    @Nullable
    private OpenTelemetryConfigurationDto openTelemetry;

    public List<DataConnectionConfigurationDto> getDataConnections() {
        return dataConnections;
    }

    public void setDataConnections(List<DataConnectionConfigurationDto> dataConnections) {
        this.dataConnections = dataConnections;
    }

    public List<DataSourceConfigurationDto> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSourceConfigurationDto> dataSources) {
        this.dataSources = dataSources;
    }

    public SecurityConfigurationDto getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfigurationDto security) {
        this.security = security;
    }

    public OpenApiConfigurationDto getOpenApi() {
        return openApi;
    }

    public void setOpenApi(OpenApiConfigurationDto openApi) {
        this.openApi = openApi;
    }

    public OpenTelemetryConfigurationDto getOpenTelemetry() {
        return openTelemetry;
    }

    public void setOpenTelemetry(OpenTelemetryConfigurationDto openTelemetry) {
        this.openTelemetry = openTelemetry;
    }
}

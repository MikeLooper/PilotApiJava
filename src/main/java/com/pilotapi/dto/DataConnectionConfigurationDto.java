package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Connection settings for the active data source. Credentials are never included.
 */
@Schema(name = "DataConnectionConfiguration", description = "Configuration for a data source connection.")
public class DataConnectionConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        description = "Gets or sets the connection timeout in seconds. A value of 0 indicates no timeout.")
    private int connectTimeout;

    @Schema(types = {"null", "string"}, description = "Gets or sets the name of the related data source settings "
        + "section.\r\nThis value will match an Application.DataSources[???].DataSourceName setting.")
    private String dataSourceName;

    @Schema(types = {"null", "string"}, description = "Gets or sets the data source host (e.g., \"localhost\")")
    private String host;

    @Schema(types = {"null", "integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        description = "Gets or sets the data source port number. A value of null indicates the default port for "
            + "the specified data source type.")
    private Integer port;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}

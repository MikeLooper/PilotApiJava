package com.pilotapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes the active data source (database, type and schema).
 */
@Schema(name = "DataSourceConfiguration", description = ConfigurationBaseDto.BASE_DESCRIPTION)
public class DataSourceConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"null", "string"}, description = "Gets or sets the name of the data source (aka: a database).")
    private String dataSource;

    @Schema(ref = "#/components/schemas/DataSourceTypes",
        description = "Gets or sets the data source type.\r\nThis will one of the DataSourceTypes options.")
    private DataSourceTypes dataSourceEnum = DataSourceTypes.UNRECOGNIZED;

    @Schema(types = {"null", "string"}, description = "Gets or sets the name of the current data source settings "
        + "section.\r\nThis value will match an Application.DataConnections[???].DataSourceName setting.")
    private String dataSourceName;

    @Schema(types = {"null", "string"}, description = "Gets or sets the data source type (e.g., \"SqlServer\", "
        + "\"PostgreSQL\").\r\nThis value must match one of the DataSourceTypes options, in string form.")
    private String dataSourceType;

    @Schema(types = {"null", "string"}, description = "Gets or sets the data source schema (e.g., \"dbo\")\r\n"
        + "For best results, do not include delimiters ([,],\") on this value.")
    private String schema;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public DataSourceTypes getDataSourceEnum() {
        return dataSourceEnum;
    }

    public void setDataSourceEnum(DataSourceTypes dataSourceEnum) {
        this.dataSourceEnum = dataSourceEnum;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}

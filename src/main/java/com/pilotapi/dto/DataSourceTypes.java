package com.pilotapi.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The supported data source types, serialized by ordinal value to match the shared contract.
 * Documented as the integer {@code DataSourceTypes} component in {@link com.pilotapi.config.OpenApiConfig}.
 */
public enum DataSourceTypes {
    UNRECOGNIZED,
    POSTGRESQL,
    SQL_SERVER;

    @JsonValue
    public int value() {
        return ordinal();
    }
}

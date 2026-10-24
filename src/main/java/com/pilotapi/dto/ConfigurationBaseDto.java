package com.pilotapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Common base for the configuration sections reported by {@code /about?show-details=true}.
 */
public abstract class ConfigurationBaseDto {

    static final String BASE_DESCRIPTION = "A base class for configuration objects.";

    @Schema(description = "Gets or sets a flag that indicates whether this settings object is active.")
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

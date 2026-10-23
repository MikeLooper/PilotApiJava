package com.pilotapi.dto;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes the metadata published in the OpenAPI document.
 */
@Schema(name = "OpenApiConfiguration", description = ConfigurationBaseDto.BASE_DESCRIPTION)
public class OpenApiConfigurationDto extends ConfigurationBaseDto {

    @Schema(description = "Gets or sets a Contact object.")
    @Nullable
    private OpenApiContactConfigurationDto contact;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenAPI Description.")
    private String description;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenAPI License.")
    private String license;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenAPI Summary.")
    private String summary;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenAPI Title.")
    private String title;

    @Schema(types = {"null", "string"}, description = "Gets or sets the OpenAPI Version.")
    private String version;

    public OpenApiContactConfigurationDto getContact() {
        return contact;
    }

    public void setContact(OpenApiContactConfigurationDto contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

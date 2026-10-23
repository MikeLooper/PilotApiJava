package com.pilotapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The contact published in the OpenAPI document.
 */
@Schema(name = "OpenApiContactConfiguration", description = ConfigurationBaseDto.BASE_DESCRIPTION)
public class OpenApiContactConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"null", "string"}, description = "Gets or sets an contactEmail.")
    private String email;

    @Schema(types = {"null", "string"}, description = "Gets or sets the contact Name.")
    private String name;

    @Schema(types = {"null", "string"}, description = "Gets or sets the URL for the contact.")
    private String url;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

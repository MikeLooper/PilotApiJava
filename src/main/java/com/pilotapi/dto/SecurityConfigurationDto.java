package com.pilotapi.dto;

import com.pilotapi.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Describes the identity provider used to validate bearer tokens.
 */
@Schema(name = "SecurityConfiguration", description = ConfigurationBaseDto.BASE_DESCRIPTION)
public class SecurityConfigurationDto extends ConfigurationBaseDto {

    @Schema(types = {"null", "string"},
        description = "Gets or sets the base URL of the Security server (e.g. \"http://localhost:11141\").")
    private String baseUrl;

    @Schema(types = {"null", "string"}, description = "Gets or sets the Security realm (e.g. \"local-realm\").")
    private String realm;

    @Schema(types = {"null", "string"}, description = "Gets or sets the Security client Id (e.g. \"local-client\").")
    private String clientId;

    @Schema(description = "Gets or sets a flag that indicates whether HTTPS is required for the Security "
        + "metadata endpoint.")
    private boolean requireHttpsMetadata;

    @Schema(types = {"integer", "string"}, format = "int32", pattern = OpenApiConfig.INTEGER_PATTERN,
        description = "Gets or sets the clock skew tolerance, in seconds, applied to token expiry validation.")
    private int clockSkewSeconds;

    @Schema(types = {"null", "string"}, description = "Gets the computed Security authority (issuer) URL, based on "
        + "string? ISecurityConfiguration.BaseUrl and string? ISecurityConfiguration.Realm.")
    private String authority;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isRequireHttpsMetadata() {
        return requireHttpsMetadata;
    }

    public void setRequireHttpsMetadata(boolean requireHttpsMetadata) {
        this.requireHttpsMetadata = requireHttpsMetadata;
    }

    public int getClockSkewSeconds() {
        return clockSkewSeconds;
    }

    public void setClockSkewSeconds(int clockSkewSeconds) {
        this.clockSkewSeconds = clockSkewSeconds;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}

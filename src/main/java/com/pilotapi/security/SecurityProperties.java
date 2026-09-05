package com.pilotapi.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private boolean active = true;
    private String providerUrl;
    private String publicProviderUrl;
    private String realm;
    private String clientId;
    private String resourceArea;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * The URL this API uses to reach the Security provider directly, for fetching signing
     * keys (e.g. "http://local-keycloak:8080" when both run on the same Docker network).
     * Must be reachable from this API's host.
     */
    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    /**
     * The externally-visible URL of the Security provider, i.e. the one clients use to
     * obtain tokens (e.g. "http://localhost:55001"). Tokens carry this value as their
     * issuer, so it is what {@link #publicIssuerUri()} validates against. Falls back to
     * {@link #getProviderUrl()} when unset, which is correct whenever this API and its
     * clients reach the Security provider the same way (e.g. local development).
     */
    public String getPublicProviderUrl() {
        return publicProviderUrl;
    }

    public void setPublicProviderUrl(String publicProviderUrl) {
        this.publicProviderUrl = publicProviderUrl;
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

    public String getResourceArea() {
        return resourceArea;
    }

    public void setResourceArea(String resourceArea) {
        this.resourceArea = resourceArea;
    }

    /**
     * The issuer/JWK-set base URI reachable from this API, used only to fetch signing
     * keys - not for issuer validation. See {@link #publicIssuerUri()} for that.
     */
    public String issuerUri() {
        return providerUrl + "/realms/" + realm;
    }

    public String jwkSetUri() {
        return issuerUri() + "/protocol/openid-connect/certs";
    }

    /**
     * The externally-visible issuer URI tokens are actually stamped with, based on
     * {@link #getPublicProviderUrl()} (or {@link #getProviderUrl()} when unset) and
     * {@link #getRealm()}. This is what token issuer validation checks against.
     */
    public String publicIssuerUri() {
        String baseUrl = StringUtils.hasText(publicProviderUrl) ? publicProviderUrl : providerUrl;
        return baseUrl + "/realms/" + realm;
    }
}

package com.pilotapi.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private boolean active = true;
    private String providerUrl;
    private String realm;
    private String clientId;
    private String resourceArea;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
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

    public String issuerUri() {
        return providerUrl + "/realms/" + realm;
    }

    public String jwkSetUri() {
        return issuerUri() + "/protocol/openid-connect/certs";
    }
}

package com.pilotapi.security;

import java.util.Set;

/**
 * Context User enrichment: identity, resolved application role, and the raw
 * roles/scopes/client attributes carried by the security token.
 */
public class EnrichedUser {

    private final String userId;
    private final ApplicationRole role;
    private final Set<String> tokenRoles;
    private final Set<String> scopes;
    private final String clientId;

    public EnrichedUser(String userId, ApplicationRole role, Set<String> tokenRoles, Set<String> scopes, String clientId) {
        this.userId = userId;
        this.role = role;
        this.tokenRoles = tokenRoles;
        this.scopes = scopes;
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public ApplicationRole getRole() {
        return role;
    }

    public Set<String> getTokenRoles() {
        return tokenRoles;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getClientId() {
        return clientId;
    }
}

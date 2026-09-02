package com.pilotapi.security;

public record AuthCheckResult(AuthOutcome outcome, String reason, EnrichedUser user) {

    public boolean isAuthorized() {
        return outcome == AuthOutcome.AUTHORIZED;
    }
}

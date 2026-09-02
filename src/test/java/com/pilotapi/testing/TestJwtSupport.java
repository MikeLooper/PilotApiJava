package com.pilotapi.testing;

import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Builds in-memory {@link Jwt} instances for tests, avoiding any dependency on a
 * live identity provider.
 */
public final class TestJwtSupport {

    private TestJwtSupport() {
    }

    public static Jwt jwtForUser(String userId) {
        return baseBuilder(userId).build();
    }

    public static Jwt jwtWithRealmRoles(String userId, List<String> realmRoles) {
        return baseBuilder(userId)
            .claim("realm_access", Map.of("roles", realmRoles))
            .build();
    }

    private static Jwt.Builder baseBuilder(String userId) {
        return Jwt.withTokenValue("test-token-" + userId)
            .header("alg", "none")
            .claim("sub", userId)
            .claim("preferred_username", userId)
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plusSeconds(300));
    }
}

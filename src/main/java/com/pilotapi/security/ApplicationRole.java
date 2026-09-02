package com.pilotapi.security;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public enum ApplicationRole {

    READ_ONLY_ROLE("read_only_role", 1,
        Set.of("GET", "HEAD", "OPTIONS", "QUERY", "TRACE")),
    READ_WRITE_ROLE("read_write_role", 2,
        Set.of("GET", "HEAD", "OPTIONS", "QUERY", "TRACE", "PATCH", "POST", "PUT")),
    ADMIN_ROLE("admin_role", 3,
        Set.of("GET", "HEAD", "OPTIONS", "QUERY", "TRACE", "PATCH", "POST", "PUT", "DELETE"));

    private final String roleName;
    private final int rank;
    private final Set<String> satisfiedMethods;

    ApplicationRole(String roleName, int rank, Set<String> satisfiedMethods) {
        this.roleName = roleName;
        this.rank = rank;
        this.satisfiedMethods = satisfiedMethods;
    }

    public String getRoleName() {
        return roleName;
    }

    public int getRank() {
        return rank;
    }

    public boolean satisfies(String httpMethod) {
        return satisfiedMethods.contains(normalize(httpMethod));
    }

    public boolean authorizes(ApplicationRole required) {
        return this.rank >= required.rank;
    }

    public static ApplicationRole requiredRoleFor(String httpMethod) {
        return switch (normalize(httpMethod)) {
            case "GET", "HEAD", "OPTIONS", "QUERY", "TRACE" -> READ_ONLY_ROLE;
            case "POST", "PUT", "PATCH" -> READ_WRITE_ROLE;
            case "DELETE" -> ADMIN_ROLE;
            default -> ADMIN_ROLE;
        };
    }

    public static Optional<ApplicationRole> fromRoleName(String roleName) {
        return Arrays.stream(values())
            .filter(role -> role.roleName.equalsIgnoreCase(roleName))
            .findFirst();
    }

    private static String normalize(String httpMethod) {
        return httpMethod == null ? "" : httpMethod.toUpperCase();
    }
}

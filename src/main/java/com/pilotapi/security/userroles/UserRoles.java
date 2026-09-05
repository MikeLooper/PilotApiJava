package com.pilotapi.security.userroles;

/**
 * Mirrors a row of the (mocked) {@code UserRoles} table.
 */
public class UserRoles {

    private final String userId;
    private final String role;

    public UserRoles(String userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}

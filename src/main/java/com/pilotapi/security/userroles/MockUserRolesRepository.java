package com.pilotapi.security.userroles;

import java.util.Map;
import java.util.Optional;

/**
 * Hard-coded stand-in for the {@code UserRoles} database table.
 */
public class MockUserRolesRepository implements UserRolesRepository {

    private static final Map<String, UserRoles> USER_ROLES = Map.of(
        "reader_user", new UserRoles("reader_user", "read_only_role"),
        "working_user", new UserRoles("working_user", "read_write_role"),
        "working_admin_user", new UserRoles("working_admin_user", "admin_role")
    );

    @Override
    public Optional<UserRoles> findByUserId(String userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(USER_ROLES.get(userId));
    }
}

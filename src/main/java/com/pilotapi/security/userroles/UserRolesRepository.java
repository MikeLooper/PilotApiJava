package com.pilotapi.security.userroles;

import java.util.Optional;

/**
 * Stands in for reading the {@code UserRoles} database table.
 */
public interface UserRolesRepository {

    Optional<UserRoles> findByUserId(String userId);
}

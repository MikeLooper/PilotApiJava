package com.pilotapi.security.userroles;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockUserRolesRepositoryTest {

    private MockUserRolesRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MockUserRolesRepository();
    }

    @Test
    void MockUserRolesRepository_findByUserId_returns_read_only_role_for_reader_user_Test() {
        // Act
        Optional<UserRoles> result = repository.findByUserId("reader_user");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("read_only_role", result.get().getRole());
    }

    @Test
    void MockUserRolesRepository_findByUserId_returns_read_write_role_for_working_user_Test() {
        // Act
        Optional<UserRoles> result = repository.findByUserId("working_user");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("read_write_role", result.get().getRole());
    }

    @Test
    void MockUserRolesRepository_findByUserId_returns_admin_role_for_working_admin_Test() {
        // Act
        Optional<UserRoles> result = repository.findByUserId("working_admin_user");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("admin_role", result.get().getRole());
    }

    @Test
    void MockUserRolesRepository_findByUserId_returns_empty_for_unknown_user_Test() {
        // Act
        Optional<UserRoles> result = repository.findByUserId("nobody");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void MockUserRolesRepository_findByUserId_returns_empty_for_null_userId_Test() {
        // Act
        Optional<UserRoles> result = repository.findByUserId(null);

        // Assert
        assertTrue(result.isEmpty());
    }
}

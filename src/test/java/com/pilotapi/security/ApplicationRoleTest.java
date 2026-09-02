package com.pilotapi.security;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationRoleTest {

    @Test
    void ApplicationRole_requiredRoleFor_maps_read_methods_to_read_only_role_Test() {
        // Act + Assert
        assertEquals(ApplicationRole.READ_ONLY_ROLE, ApplicationRole.requiredRoleFor("GET"));
        assertEquals(ApplicationRole.READ_ONLY_ROLE, ApplicationRole.requiredRoleFor("head"));
        assertEquals(ApplicationRole.READ_ONLY_ROLE, ApplicationRole.requiredRoleFor("OPTIONS"));
        assertEquals(ApplicationRole.READ_ONLY_ROLE, ApplicationRole.requiredRoleFor("QUERY"));
        assertEquals(ApplicationRole.READ_ONLY_ROLE, ApplicationRole.requiredRoleFor("TRACE"));
    }

    @Test
    void ApplicationRole_requiredRoleFor_maps_write_methods_to_read_write_role_Test() {
        // Act + Assert
        assertEquals(ApplicationRole.READ_WRITE_ROLE, ApplicationRole.requiredRoleFor("POST"));
        assertEquals(ApplicationRole.READ_WRITE_ROLE, ApplicationRole.requiredRoleFor("PUT"));
        assertEquals(ApplicationRole.READ_WRITE_ROLE, ApplicationRole.requiredRoleFor("PATCH"));
    }

    @Test
    void ApplicationRole_requiredRoleFor_maps_delete_to_admin_role_Test() {
        // Act + Assert
        assertEquals(ApplicationRole.ADMIN_ROLE, ApplicationRole.requiredRoleFor("DELETE"));
    }

    @Test
    void ApplicationRole_requiredRoleFor_defaults_unmapped_method_to_admin_role_Test() {
        // Act + Assert
        assertEquals(ApplicationRole.ADMIN_ROLE, ApplicationRole.requiredRoleFor("CONNECT"));
        assertEquals(ApplicationRole.ADMIN_ROLE, ApplicationRole.requiredRoleFor(null));
    }

    @Test
    void ApplicationRole_authorizes_returns_true_when_rank_is_equal_or_higher_Test() {
        // Act + Assert
        assertTrue(ApplicationRole.ADMIN_ROLE.authorizes(ApplicationRole.READ_WRITE_ROLE));
        assertTrue(ApplicationRole.READ_WRITE_ROLE.authorizes(ApplicationRole.READ_WRITE_ROLE));
        assertFalse(ApplicationRole.READ_ONLY_ROLE.authorizes(ApplicationRole.READ_WRITE_ROLE));
    }

    @Test
    void ApplicationRole_satisfies_reflects_the_requirements_table_Test() {
        // Act + Assert
        assertTrue(ApplicationRole.READ_ONLY_ROLE.satisfies("GET"));
        assertFalse(ApplicationRole.READ_ONLY_ROLE.satisfies("POST"));
        assertTrue(ApplicationRole.READ_WRITE_ROLE.satisfies("POST"));
        assertFalse(ApplicationRole.READ_WRITE_ROLE.satisfies("DELETE"));
        assertTrue(ApplicationRole.ADMIN_ROLE.satisfies("DELETE"));
    }

    @Test
    void ApplicationRole_fromRoleName_finds_case_insensitive_match_Test() {
        // Act
        Optional<ApplicationRole> result = ApplicationRole.fromRoleName("READ_WRITE_ROLE");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ApplicationRole.READ_WRITE_ROLE, result.get());
    }

    @Test
    void ApplicationRole_fromRoleName_returns_empty_for_unknown_role_Test() {
        // Act
        Optional<ApplicationRole> result = ApplicationRole.fromRoleName("super_role");

        // Assert
        assertTrue(result.isEmpty());
    }
}

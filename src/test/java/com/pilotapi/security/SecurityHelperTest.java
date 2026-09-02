package com.pilotapi.security;

import com.pilotapi.security.userroles.UserRoles;
import com.pilotapi.security.userroles.UserRolesRepository;
import com.pilotapi.testing.TestJwtSupport;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHelperTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserRolesRepository userRolesRepository;

    @Mock
    private HttpServletRequest request;

    private SecurityHelper securityHelper;

    @BeforeEach
    void setUp() {
        SecurityProperties properties = new SecurityProperties();
        properties.setResourceArea("account");
        securityHelper = new SecurityHelper(jwtDecoder, userRolesRepository, properties);
        SecurityContextHolder.clearContext();
    }

    @Test
    void SecurityHelper_extractBearerToken_returns_empty_when_header_missing_Test() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        Optional<String> result = securityHelper.extractBearerToken(request);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void SecurityHelper_extractBearerToken_returns_empty_when_header_lacks_bearer_prefix_Test() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        // Act
        Optional<String> result = securityHelper.extractBearerToken(request);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void SecurityHelper_extractBearerToken_returns_token_for_valid_header_Test() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer my-token");

        // Act
        Optional<String> result = securityHelper.extractBearerToken(request);

        // Assert
        assertEquals("my-token", result.get());
    }

    @Test
    void SecurityHelper_check_returns_unauthenticated_when_token_missing_Test() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        AuthCheckResult result = securityHelper.check(request);

        // Assert
        assertEquals(AuthOutcome.UNAUTHENTICATED, result.outcome());
    }

    @Test
    void SecurityHelper_check_returns_unauthenticated_when_token_invalid_Test() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer bad-token");
        when(jwtDecoder.decode(anyString())).thenThrow(new BadJwtException("expired"));

        // Act
        AuthCheckResult result = securityHelper.check(request);

        // Assert
        assertEquals(AuthOutcome.UNAUTHENTICATED, result.outcome());
    }

    @Test
    void SecurityHelper_check_returns_unknown_user_when_no_role_entry_Test() {
        // Arrange
        Jwt jwt = TestJwtSupport.jwtForUser("nobody");
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(userRolesRepository.findByUserId("nobody")).thenReturn(Optional.empty());

        // Act
        AuthCheckResult result = securityHelper.check(request);

        // Assert
        assertEquals(AuthOutcome.UNKNOWN_USER, result.outcome());
    }

    @Test
    void SecurityHelper_check_returns_insufficient_role_when_role_rank_too_low_Test() {
        // Arrange
        Jwt jwt = TestJwtSupport.jwtForUser("reader_user");
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getMethod()).thenReturn("DELETE");
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(userRolesRepository.findByUserId("reader_user"))
            .thenReturn(Optional.of(new UserRoles("reader_user", "read_only_role")));

        // Act
        AuthCheckResult result = securityHelper.check(request);

        // Assert
        assertEquals(AuthOutcome.INSUFFICIENT_ROLE, result.outcome());
        assertEquals("reader_user", result.user().getUserId());
    }

    @Test
    void SecurityHelper_check_returns_authorized_and_populates_security_context_Test() {
        // Arrange
        Jwt jwt = TestJwtSupport.jwtWithRealmRoles("working_admin_user", List.of("offline_access"));
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getMethod()).thenReturn("DELETE");
        when(jwtDecoder.decode("token")).thenReturn(jwt);
        when(userRolesRepository.findByUserId("working_admin_user"))
            .thenReturn(Optional.of(new UserRoles("working_admin_user", "admin_role")));

        // Act
        AuthCheckResult result = securityHelper.check(request);

        // Assert
        assertEquals(AuthOutcome.AUTHORIZED, result.outcome());
        assertEquals(ApplicationRole.ADMIN_ROLE, result.user().getRole());
        assertTrue(result.user().getTokenRoles().contains("offline_access"));
        assertEquals("working_admin_user",
            ((EnrichedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId());
    }

    @Test
    void SecurityHelper_redact_masks_password_in_json_Test() {
        // Act
        String redacted = securityHelper.redact("{\"username\":\"bob\",\"password\":\"secret123\"}");

        // Assert
        assertTrue(redacted.contains("***REDACTED***"));
        assertTrue(!redacted.contains("secret123"));
    }

    @Test
    void SecurityHelper_redact_returns_null_for_null_input_Test() {
        // Act + Assert
        assertNull(securityHelper.redact(null));
    }

    @Test
    void SecurityHelper_buildWarningHeaderValue_includes_reason_Test() {
        // Act
        String header = securityHelper.buildWarningHeaderValue("Missing token");

        // Assert
        assertTrue(header.contains("Missing token"));
        assertTrue(header.startsWith("199"));
    }
}

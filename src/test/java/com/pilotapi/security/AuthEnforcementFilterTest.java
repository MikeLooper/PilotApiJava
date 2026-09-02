package com.pilotapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthEnforcementFilterTest {

    @Mock
    private SecurityHelper securityHelper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private AuthEnforcementFilter filter;
    private SecurityProperties securityProperties;

    @BeforeEach
    void setUp() {
        securityProperties = new SecurityProperties();
        filter = new AuthEnforcementFilter(securityHelper, securityProperties);
    }

    @Test
    void AuthEnforcementFilter_doFilterInternal_skips_non_v1_paths_Test() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/healthcheck");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(securityHelper, never()).check(request);
    }

    @Test
    void AuthEnforcementFilter_doFilterInternal_continues_chain_when_authorized_Test() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v1/categories/get-all");
        AuthCheckResult authorized = new AuthCheckResult(AuthOutcome.AUTHORIZED, "Authorized", null);
        when(securityHelper.check(request)).thenReturn(authorized);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setHeader(eq("Warning"), anyString());
    }

    @Test
    void AuthEnforcementFilter_doFilterInternal_blocks_with_401_when_active_and_unauthenticated_Test() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v1/categories/get-all");
        securityProperties.setActive(true);
        AuthCheckResult failure = new AuthCheckResult(AuthOutcome.UNAUTHENTICATED, "Missing token", null);
        when(securityHelper.check(request)).thenReturn(failure);

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(401);
        assertTrue(body.toString().contains("Missing token"));
    }

    @Test
    void AuthEnforcementFilter_doFilterInternal_blocks_with_403_when_active_and_insufficient_role_Test() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v1/categories/delete/1");
        securityProperties.setActive(true);
        AuthCheckResult failure = new AuthCheckResult(AuthOutcome.INSUFFICIENT_ROLE, "Role too low", null);
        when(securityHelper.check(request)).thenReturn(failure);

        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(403);
    }

    @Test
    void AuthEnforcementFilter_doFilterInternal_allows_through_with_warning_when_inactive_Test() throws Exception {
        // Arrange
        when(request.getRequestURI()).thenReturn("/v1/categories/get-all");
        securityProperties.setActive(false);
        AuthCheckResult failure = new AuthCheckResult(AuthOutcome.UNAUTHENTICATED, "Missing token", null);
        when(securityHelper.check(request)).thenReturn(failure);
        when(securityHelper.buildWarningHeaderValue("Missing token")).thenReturn("199 pilot-api \"Missing token\"");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setHeader("Warning", "199 pilot-api \"Missing token\"");
        verify(filterChain, times(1)).doFilter(request, response);
    }
}

package com.pilotapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Orchestrates auth enforcement for domain (/v1/**) endpoints only. All decision
 * logic lives in {@link SecurityHelper}; this filter applies the active-flag
 * enforce-vs-warn behavior around it.
 */
public class AuthEnforcementFilter extends OncePerRequestFilter {

    private static final String SECURED_PATH_PREFIX = "/v1/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SecurityHelper securityHelper;
    private final SecurityProperties securityProperties;

    public AuthEnforcementFilter(SecurityHelper securityHelper, SecurityProperties securityProperties) {
        this.securityHelper = securityHelper;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith(SECURED_PATH_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthCheckResult result = securityHelper.check(request);
        boolean active = securityProperties.isActive();

        if (result.isAuthorized()) {
            securityHelper.logOutcome(result, request, false);
            filterChain.doFilter(request, response);
            return;
        }

        securityHelper.logOutcome(result, request, active);

        if (!active) {
            response.setHeader("Warning", securityHelper.buildWarningHeaderValue(result.reason()));
            filterChain.doFilter(request, response);
            return;
        }

        writeError(response, result);
    }

    /**
     * Per the OpenAPI spec, 401/403 bodies are a JSON string (not a ProblemDetails object).
     */
    private void writeError(HttpServletResponse response, AuthCheckResult result) throws IOException {
        HttpStatus status = result.outcome() == AuthOutcome.UNAUTHENTICATED
            ? HttpStatus.UNAUTHORIZED
            : HttpStatus.FORBIDDEN;

        String body = result.reason() == null || result.reason().isBlank()
            ? status.getReasonPhrase()
            : result.reason();

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        OBJECT_MAPPER.writeValue(response.getWriter(), body);
    }
}

package com.pilotapi.security;

import com.pilotapi.security.userroles.UserRoles;
import com.pilotapi.security.userroles.UserRolesRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Centralizes all authentication/authorization logic: bearer token extraction and
 * validation, application-role resolution, context-user enrichment, method-based
 * authorization, redaction, and auth logging.
 */
public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("(?i)(\"?(password|pwd|pass)\"?\\s*[:=]\\s*\"?)([^\",&\\s]+)");

    private final JwtDecoder jwtDecoder;
    private final UserRolesRepository userRolesRepository;
    private final SecurityProperties securityProperties;

    public SecurityHelper(JwtDecoder jwtDecoder, UserRolesRepository userRolesRepository, SecurityProperties securityProperties) {
        this.jwtDecoder = jwtDecoder;
        this.userRolesRepository = userRolesRepository;
        this.securityProperties = securityProperties;
    }

    public Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        String token = header.substring(BEARER_PREFIX.length()).trim();
        return StringUtils.hasText(token) ? Optional.of(token) : Optional.empty();
    }

    public AuthCheckResult check(HttpServletRequest request) {
        Optional<String> rawToken = extractBearerToken(request);
        if (rawToken.isEmpty()) {
            return new AuthCheckResult(AuthOutcome.UNAUTHENTICATED, "Missing or malformed Authorization header", null);
        }

        Jwt jwt;
        try {
            jwt = jwtDecoder.decode(rawToken.get());
        } catch (JwtException ex) {
            return new AuthCheckResult(AuthOutcome.UNAUTHENTICATED, "Invalid token: " + ex.getMessage(), null);
        }

        String userId = resolveUserId(jwt);
        Optional<UserRoles> userRolesEntry = userRolesRepository.findByUserId(userId);
        if (userRolesEntry.isEmpty()) {
            return new AuthCheckResult(AuthOutcome.UNKNOWN_USER, "No application role found for user '" + userId + "'", null);
        }

        Optional<ApplicationRole> resolvedRole = ApplicationRole.fromRoleName(userRolesEntry.get().getRole());
        if (resolvedRole.isEmpty()) {
            return new AuthCheckResult(AuthOutcome.UNKNOWN_USER,
                "Unrecognized role '" + userRolesEntry.get().getRole() + "' for user '" + userId + "'", null);
        }

        EnrichedUser enrichedUser = buildEnrichedUser(jwt, userId, resolvedRole.get());
        applySecurityContext(enrichedUser);

        ApplicationRole requiredRole = ApplicationRole.requiredRoleFor(request.getMethod());
        if (!enrichedUser.getRole().authorizes(requiredRole)) {
            return new AuthCheckResult(AuthOutcome.INSUFFICIENT_ROLE,
                "Role '" + enrichedUser.getRole().getRoleName() + "' does not satisfy required role '" + requiredRole.getRoleName() + "'",
                enrichedUser);
        }

        return new AuthCheckResult(AuthOutcome.AUTHORIZED, "Authorized", enrichedUser);
    }

    public void logOutcome(AuthCheckResult result, HttpServletRequest request, boolean blocked) {
        String jwt = redact(extractBearerToken(request).orElse(""));
        if (result.isAuthorized()) {
            LOGGER.info("Authentication succeeded for user '{}' with role '{}' on {} {} (token={})",
                result.user().getUserId(), result.user().getRole().getRoleName(),
                request.getMethod(), request.getRequestURI(), jwt);
        } else {
            LOGGER.warn("Authentication/authorization failed on {} {}: {} ({}, token={})",
                request.getMethod(), request.getRequestURI(), redact(result.reason()),
                blocked ? "blocked" : "allowed via inactive security", jwt);
        }
    }

    public String redact(String raw) {
        if (raw == null) {
            return null;
        }
        Matcher matcher = PASSWORD_PATTERN.matcher(raw);
        return matcher.replaceAll("$1***REDACTED***");
    }

    public String buildWarningHeaderValue(String reason) {
        return "199 pilot-api \"" + redact(reason) + "\"";
    }

    private String resolveUserId(Jwt jwt) {
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        return StringUtils.hasText(preferredUsername) ? preferredUsername : jwt.getSubject();
    }

    private EnrichedUser buildEnrichedUser(Jwt jwt, String userId, ApplicationRole role) {
        Set<String> tokenRoles = extractTokenRoles(jwt);
        Set<String> scopes = extractScopes(jwt);
        String clientId = firstNonBlank(jwt.getClaimAsString("azp"), jwt.getClaimAsString("client_id"));
        return new EnrichedUser(userId, role, tokenRoles, scopes, clientId);
    }

    private Set<String> extractTokenRoles(Jwt jwt) {
        Set<String> roles = new LinkedHashSet<>();

        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> realmRoles) {
            realmRoles.forEach(role -> roles.add(String.valueOf(role)));
        }

        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            Object area = resourceAccess.get(securityProperties.getResourceArea());
            if (area instanceof Map<?, ?> areaMap && areaMap.get("roles") instanceof List<?> areaRoles) {
                areaRoles.forEach(role -> roles.add(String.valueOf(role)));
            }
        }

        return roles;
    }

    private Set<String> extractScopes(Jwt jwt) {
        String scopeClaim = jwt.getClaimAsString("scope");
        if (!StringUtils.hasText(scopeClaim)) {
            return Set.of();
        }
        return Set.of(scopeClaim.split("\\s+"));
    }

    private void applySecurityContext(EnrichedUser enrichedUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(enrichedUser.getRole().getRoleName()));
        enrichedUser.getTokenRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        Authentication authentication = new UsernamePasswordAuthenticationToken(enrichedUser, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}

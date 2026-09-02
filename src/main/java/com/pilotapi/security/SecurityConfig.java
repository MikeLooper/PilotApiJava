package com.pilotapi.security;

import com.pilotapi.security.userroles.MockUserRolesRepository;
import com.pilotapi.security.userroles.UserRolesRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Composition root for security: builds the token decoder, the mock role
 * repository, {@link SecurityHelper}, and the filter chain that runs
 * {@link AuthEnforcementFilter} ahead of the rest of the chain. Kept
 * self-contained so importing just this class (e.g. in a {@code @WebMvcTest})
 * is enough to satisfy every dependency.
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    @Bean
    public UserRolesRepository userRolesRepository() {
        return new MockUserRolesRepository();
    }

    @Bean
    public JwtDecoder jwtDecoder(SecurityProperties securityProperties) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(securityProperties.jwkSetUri()).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(securityProperties.issuerUri()));
        return decoder;
    }

    @Bean
    public SecurityHelper securityHelper(JwtDecoder jwtDecoder, UserRolesRepository userRolesRepository,
                                          SecurityProperties securityProperties) {
        return new SecurityHelper(jwtDecoder, userRolesRepository, securityProperties);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityHelper securityHelper,
                                                     SecurityProperties securityProperties) throws Exception {
        AuthEnforcementFilter authEnforcementFilter = new AuthEnforcementFilter(securityHelper, securityProperties);

        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(authEnforcementFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

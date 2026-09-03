package com.pilotapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    /**
     * Name of the security scheme referenced on secured operations. Matches
     * {@link com.pilotapi.security.AuthEnforcementFilter}'s bearer-token check.
     */
    public static final String BEARER_AUTH_SCHEME = "bearerAuth";
    private static final String SECURED_PATH_PREFIX = "/v1/";
    private static final String LICENSE_URL = "https://opensource.org";

    @Bean
    public OpenAPI customOpenApi(OpenApiProperties openApiProperties, ApplicationMetadataProperties metadata) {
        return new OpenAPI()
            .info(new Info()
                .title(openApiProperties.getTitle())
                .version("Version: " + metadata.getBuildVersion())
                .description("Description: " + openApiProperties.getDescription())
                .summary("Summary: " + openApiProperties.getSummary())
                .contact(new Contact()
                    .name("Contact: " + openApiProperties.getContact().getName())
                    .email(openApiProperties.getContact().getEmail())
                    .url(openApiProperties.getContact().getUrl()))
                .license(new License()
                    .name("License: " + openApiProperties.getLicense())
                    .url(LICENSE_URL)))
            .components(new Components()
                .addSecuritySchemes(BEARER_AUTH_SCHEME, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Bearer JWT required for all /v1/** domain endpoints. "
                        + "Not required for About/Healthcheck.")));
    }

    /**
     * Marks every operation under the {@code /v1/**} prefix as requiring the
     * {@value #BEARER_AUTH_SCHEME} scheme, so Swagger UI shows a lock icon and
     * accepts an Authorize token for exactly the endpoints
     * {@link com.pilotapi.security.AuthEnforcementFilter} enforces auth on.
     * System endpoints (About, Healthcheck) are left unsecured.
     */
    @Bean
    public OperationCustomizer securedPathOperationCustomizer() {
        return (operation, handlerMethod) -> {
            if (isSecuredPath(handlerMethod)) {
                operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
            }
            return operation;
        };
    }

    private boolean isSecuredPath(HandlerMethod handlerMethod) {
        RequestMapping classMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
        if (classMapping == null || classMapping.value().length == 0) {
            return false;
        }
        for (String path : classMapping.value()) {
            if (path.startsWith(SECURED_PATH_PREFIX)) {
                return true;
            }
        }
        return false;
    }
}

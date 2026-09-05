package com.pilotapi.config;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    void OpenApiConfigTest_customOpenApi_registersBearerSecurityScheme_Test() {
        ApplicationMetadataProperties metadata = new ApplicationMetadataProperties();
        metadata.setBuildVersion("1.0.0");
        OpenApiProperties properties = new OpenApiProperties();

        OpenAPI openApi = openApiConfig.customOpenApi(properties, metadata);
        SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_AUTH_SCHEME);

        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
    }

    @Test
    void OpenApiConfigTest_operationCustomizer_addsBearerSecurity_forV1Controller_Test() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.securedPathOperationCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(new SecuredDummyController(),
            SecuredDummyController.class.getMethod("get"));

        Operation operation = customizer.customize(new Operation(), handlerMethod);

        assertEquals(1, operation.getSecurity().size());
        assertTrue(operation.getSecurity().get(0).containsKey(OpenApiConfig.BEARER_AUTH_SCHEME));
    }

    @Test
    void OpenApiConfigTest_operationCustomizer_leavesSystemControllerUnsecured_Test() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.securedPathOperationCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(new UnsecuredDummyController(),
            UnsecuredDummyController.class.getMethod("get"));

        Operation operation = customizer.customize(new Operation(), handlerMethod);

        assertNull(operation.getSecurity());
    }

    @RequestMapping("/v1/dummy")
    private static class SecuredDummyController {
        @GetMapping
        public void get() {
        }
    }

    @RequestMapping
    private static class UnsecuredDummyController {
        @GetMapping
        public void get() {
        }
    }
}

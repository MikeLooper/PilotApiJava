package com.pilotapi.config;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    void OpenApiConfigTest_customOpenApi_registersBearerSecurityScheme_Test() {
        OpenApiProperties properties = new OpenApiProperties();

        OpenAPI openApi = openApiConfig.customOpenApi(properties, new ApplicationMetadataProperties());
        SecurityScheme scheme = openApi.getComponents().getSecuritySchemes().get(OpenApiConfig.BEARER_AUTH_SCHEME);

        assertEquals("Bearer", OpenApiConfig.BEARER_AUTH_SCHEME);
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("JWT", scheme.getBearerFormat());
        assertEquals("JWT bearer authentication", scheme.getDescription());
    }

    @Test
    void OpenApiConfigTest_customOpenApi_usesUnprefixedInfoAndApiVersion_Test() {
        OpenApiProperties properties = new OpenApiProperties();
        properties.setTitle("PilotApiJava");
        properties.setLicense("MIT");
        properties.getContact().setName("Michael Looper");
        ApplicationMetadataProperties metadata = new ApplicationMetadataProperties();
        metadata.setBuildVersion("1.2.3");

        OpenAPI openApi = openApiConfig.customOpenApi(properties, metadata);

        assertEquals("PilotApiJava", openApi.getInfo().getTitle());
        assertEquals("1.2.3", openApi.getInfo().getVersion());
        assertEquals("MIT", openApi.getInfo().getLicense().getName());
        assertEquals("Michael Looper", openApi.getInfo().getContact().getName());
    }

    @Test
    void OpenApiConfigTest_customOpenApi_registersSharedComponents_Test() {
        OpenAPI openApi = openApiConfig.customOpenApi(new OpenApiProperties(), new ApplicationMetadataProperties());
        Components components = openApi.getComponents();

        assertEquals(Set.of("ApiVersionHeader", "CorrelationIdHeader", "PageQuery", "PageSizeQuery"),
            components.getParameters().keySet());
        assertEquals(Set.of("BadRequest", "Unauthorized", "Forbidden", "InternalServerError"),
            components.getResponses().keySet());
        assertEquals(Set.of("DataSourceTypes", "ProblemDetails", "ProblemDetailsBadRequest"),
            components.getSchemas().keySet());
        assertEquals(List.of("type", "title"), components.getSchemas().get("ProblemDetails").getRequired());
        assertEquals(9, openApi.getTags().size());
    }

    @Test
    void OpenApiConfigTest_operationCustomizer_securesV1Operation_andAddsErrorResponses_Test() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.contractOperationCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(new SecuredDummyController(),
            SecuredDummyController.class.getMethod("get"));

        Operation operation = customizer.customize(new Operation().operationId("get_1"), handlerMethod);

        assertEquals(1, operation.getSecurity().size());
        assertTrue(operation.getSecurity().get(0).containsKey(OpenApiConfig.BEARER_AUTH_SCHEME));
        assertEquals(Set.of("400", "401", "403", "500"), operation.getResponses().keySet());
        assertEquals("#/components/responses/BadRequest", operation.getResponses().get("400").get$ref());
        assertNull(operation.getOperationId());
    }

    @Test
    void OpenApiConfigTest_operationCustomizer_leavesSystemControllerUnsecured_Test() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.contractOperationCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(new UnsecuredDummyController(),
            UnsecuredDummyController.class.getMethod("get"));

        Operation operation = customizer.customize(new Operation(), handlerMethod);

        assertNull(operation.getSecurity());
        assertEquals(Set.of("500"), operation.getResponses().keySet());
    }

    @Test
    void OpenApiConfigTest_operationCustomizer_replacesSharedParametersAndDropsAccept_Test() throws NoSuchMethodException {
        OperationCustomizer customizer = openApiConfig.contractOperationCustomizer();
        HandlerMethod handlerMethod = new HandlerMethod(new SecuredDummyController(),
            SecuredDummyController.class.getMethod("get"));
        Parameter pathParameter = new Parameter().name("id").in("path");
        Operation operation = new Operation().parameters(new ArrayList<>(List.of(
            new Parameter().name("page").in("query"),
            pathParameter,
            new Parameter().name("Accept").in("header"),
            new Parameter().name("X-Api-Version").in("header"),
            new Parameter().name("X-Correlation-Id").in("header"))));

        List<Parameter> parameters = customizer.customize(operation, handlerMethod).getParameters();

        assertEquals(4, parameters.size());
        assertEquals("#/components/parameters/PageQuery", parameters.get(0).get$ref());
        assertSame(pathParameter, parameters.get(1));
        assertEquals("#/components/parameters/ApiVersionHeader", parameters.get(2).get$ref());
        assertEquals("#/components/parameters/CorrelationIdHeader", parameters.get(3).get$ref());
    }

    @Test
    void OpenApiConfigTest_nullableReferenceCustomizer_wrapsNullableReferenceInOneOf_Test() throws NoSuchFieldException {
        PropertyCustomizer customizer = openApiConfig.nullableReferencePropertyCustomizer();
        Schema<Object> property = new JsonSchema().$ref("#/components/schemas/Child");
        property.setDescription("A child.");

        Schema<?> result = customizer.customize(property, annotatedType("nullableChild"));

        assertEquals(2, result.getOneOf().size());
        assertEquals(Set.of("null"), result.getOneOf().get(0).getTypes());
        assertEquals("#/components/schemas/Child", result.getOneOf().get(1).get$ref());
        assertEquals("A child.", result.getOneOf().get(1).getDescription());
    }

    @Test
    void OpenApiConfigTest_nullableReferenceCustomizer_leavesNonNullableReferenceUnchanged_Test() throws NoSuchFieldException {
        PropertyCustomizer customizer = openApiConfig.nullableReferencePropertyCustomizer();
        Schema<Object> property = new JsonSchema().$ref("#/components/schemas/Child");

        Schema<?> result = customizer.customize(property, annotatedType("child"));

        assertSame(property, result);
    }

    @Test
    void OpenApiConfigTest_referenceCleanupCustomizer_keepsOnlyDescriptionBesideRef_Test() {
        Schema<Object> enumReference = new JsonSchema().$ref("#/components/schemas/DataSourceTypes");
        enumReference.setDescription("The type.");
        enumReference.setTypes(Set.of("string"));
        enumReference.setEnum(List.of("0", "1"));
        Schema<Object> plain = new JsonSchema();
        plain.setTypes(Set.of("string"));
        Schema<Object> component = new JsonSchema();
        component.setProperties(new java.util.LinkedHashMap<>(Map.of("type", enumReference, "name", plain)));
        OpenAPI openApi = new OpenAPI().components(new Components().addSchemas("Parent", component));

        openApiConfig.referencePropertyCleanupCustomizer().customise(openApi);

        Schema<?> cleaned = component.getProperties().get("type");
        assertEquals("#/components/schemas/DataSourceTypes", cleaned.get$ref());
        assertEquals("The type.", cleaned.getDescription());
        assertNull(cleaned.getTypes());
        assertNull(cleaned.getEnum());
        assertSame(plain, component.getProperties().get("name"));
    }

    private static AnnotatedType annotatedType(String fieldName) throws NoSuchFieldException {
        Annotation[] annotations = Parent.class.getDeclaredField(fieldName).getAnnotations();
        return new AnnotatedType(Object.class).ctxAnnotations(annotations);
    }

    private static class Parent {
        @Nullable
        private Object nullableChild;

        private Object child;
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

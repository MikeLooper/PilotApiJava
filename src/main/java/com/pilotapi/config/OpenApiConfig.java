package com.pilotapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.JsonSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.core.converter.AnnotatedType;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Builds the published OpenAPI document so it matches the shared contract in
 * {@code shared/PilotSharedSource/OpenAPI/PilotApi_v1.yaml}: the reusable
 * parameters, error responses and ProblemDetails schemas live here as
 * components, and {@link #contractOperationCustomizer()} wires them into every
 * operation.
 */
@Configuration
@EnableConfigurationProperties({OpenApiProperties.class, ApplicationMetadataProperties.class})
public class OpenApiConfig {

    /**
     * Name of the security scheme referenced on secured operations. Matches
     * {@link com.pilotapi.security.AuthEnforcementFilter}'s bearer-token check.
     */
    public static final String BEARER_AUTH_SCHEME = "Bearer";

    /** The API contract version and default {@code X-Api-Version} header value. */
    public static final String API_VERSION_PLACEHOLDER = "1.0.0";

    /** Pattern the contract applies to integers that may also be sent as strings. */
    public static final String INTEGER_PATTERN = "^-?(?:0|[1-9]\\d*)$";
    /** Pattern the contract applies to money decimals (up to 4 fraction digits) that may also be sent as strings. */
    public static final String DECIMAL_PATTERN = "^-?(?:0|[1-9]\\d*)(?:\\.\\d{1,4})?$";
    /** Pattern the contract applies to floats (exponent allowed) that may also be sent as strings. */
    public static final String FLOAT_PATTERN = "^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?$";

    /** Smallest identifier value the contract allows. */
    public static final String ID_MINIMUM = "1";
    /** Largest int32 value, the contract's upper bound for int32 values. */
    public static final String INT32_MAXIMUM = "2147483647";
    /** Most characters an int32 value can have when sent as a string. */
    public static final int INT32_MAX_LENGTH = 10;
    /** Largest int16 value, the contract's upper bound for int16 values. */
    public static final String INT16_MAXIMUM = "32767";
    /** Most characters an int16 value can have when sent as a string. */
    public static final int INT16_MAX_LENGTH = 5;
    /** Smallest value of the SQL Server MONEY type. */
    public static final String MONEY_MINIMUM = "-922337203685477.5808";
    /** Largest value of the SQL Server MONEY type. */
    public static final String MONEY_MAXIMUM = "922337203685477.5807";
    /** Length of a customer identifier. */
    public static final int CUSTOMER_ID_LENGTH = 5;
    /** Range the contract documents for date-time values stored in a SQL Server DATETIME column. */
    public static final String DATETIME_RANGE_DESCRIPTION =
        "Valid range is 1753-01-01 through 9999-12-31 (SQL Server DATETIME).";
    /** Contract description of a column whose database default is 0. */
    public static final String DEFAULT_ZERO_DESCRIPTION = "Database default is 0.";

    public static final String TAG_SYSTEM = "System";
    public static final String TAG_CATEGORIES = "Categories";
    public static final String TAG_CUSTOMERS = "Customers";
    public static final String TAG_EMPLOYEES = "Employees";
    public static final String TAG_ORDER_DETAILS = "OrderDetails";
    public static final String TAG_ORDERS = "Orders";
    public static final String TAG_PRODUCTS = "Products";
    public static final String TAG_SHIPPERS = "Shippers";
    public static final String TAG_SUPPLIERS = "Suppliers";

    private static final List<String> TAG_ORDER = List.of(TAG_SYSTEM, TAG_CATEGORIES, TAG_CUSTOMERS,
        TAG_EMPLOYEES, TAG_ORDER_DETAILS, TAG_ORDERS, TAG_PRODUCTS, TAG_SHIPPERS, TAG_SUPPLIERS);

    private static final String SECURED_PATH_PREFIX = "/v1/";
    private static final String LICENSE_URL = "https://opensource.org";
    private static final String JSON = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

    private static final String API_VERSION_HEADER = "X-Api-Version";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String ACCEPT_HEADER = "Accept";
    static final int API_VERSION_MAX_LENGTH = 15;
    private static final int CORRELATION_ID_MAX_LENGTH = 40;

    private static final String PARAMETERS_REF = "#/components/parameters/";
    private static final String RESPONSES_REF = "#/components/responses/";
    private static final String SCHEMAS_REF = "#/components/schemas/";

    /** Shared parameter components, keyed by the parameter name they replace. */
    private static final Map<String, String> PARAMETER_COMPONENTS = Map.of(
        API_VERSION_HEADER, "ApiVersionHeader",
        CORRELATION_ID_HEADER, "CorrelationIdHeader",
        "page", "PageQuery",
        "pageSize", "PageSizeQuery");

    @Bean
    public OpenAPI customOpenApi(OpenApiProperties openApiProperties, ApplicationMetadataProperties metadata) {
        return new OpenAPI()
            .info(new Info()
                .title(openApiProperties.getTitle())
                .version(API_VERSION_PLACEHOLDER)
                .description(openApiProperties.getDescription())
                .summary(openApiProperties.getSummary())
                .contact(new Contact()
                    .name(openApiProperties.getContact().getName())
                    .email(openApiProperties.getContact().getEmail())
                    .url(openApiProperties.getContact().getUrl()))
                .license(new License()
                    .name(openApiProperties.getLicense())
                    .url(LICENSE_URL)))
            .tags(TAG_ORDER.stream().map(name -> new Tag().name(name)).toList())
            .components(new Components()
                .parameters(parameterComponents())
                .responses(responseComponents())
                .schemas(schemaComponents())
                .addSecuritySchemes(BEARER_AUTH_SCHEME, new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .description("JWT bearer authentication")
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }

    /**
     * Aligns every generated operation with the contract:
     * <ul>
     *   <li>drops the generated operationId and the {@code Accept} header (which OpenAPI says to ignore);</li>
     *   <li>replaces the shared header/paging parameters with references to their components;</li>
     *   <li>adds the standard error responses, and for {@code /v1/**} operations the
     *       {@value #BEARER_AUTH_SCHEME} security requirement, matching what
     *       {@link com.pilotapi.security.AuthEnforcementFilter} enforces. System endpoints
     *       (About, Healthcheck) are left unsecured.</li>
     * </ul>
     */
    @Bean
    public OperationCustomizer contractOperationCustomizer() {
        return (operation, handlerMethod) -> {
            operation.setOperationId(null);
            replaceSharedParameters(operation);

            boolean secured = isSecuredPath(handlerMethod);
            ApiResponses responses = operation.getResponses() == null ? new ApiResponses() : operation.getResponses();
            if (secured) {
                responses.addApiResponse("400", responseRef("BadRequest"));
                responses.addApiResponse("401", responseRef("Unauthorized"));
                responses.addApiResponse("403", responseRef("Forbidden"));
            }
            responses.addApiResponse("500", responseRef("InternalServerError"));
            operation.setResponses(responses);

            if (secured) {
                operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH_SCHEME));
            }
            return operation;
        };
    }

    /**
     * Expresses a component-typed property marked {@link Nullable @Nullable} as
     * {@code oneOf: [{type: "null"}, {$ref}]}, the OpenAPI 3.1 form the contract uses for
     * nullable references (a {@code "null"} type cannot sit beside a {@code $ref}).
     */
    @Bean
    public PropertyCustomizer nullableReferencePropertyCustomizer() {
        return (property, type) -> {
            if (property == null || property.get$ref() == null || !isMarkedNullable(type)) {
                return property;
            }
            Schema<Object> nullable = new JsonSchema();
            nullable.setOneOf(List.of(schema("null"), reference(property.get$ref(), property.getDescription())));
            return nullable;
        };
    }

    /**
     * Leaves only a description beside each {@code $ref} property, as the contract does.
     * Swagger copies an enum's type and values next to the reference after property
     * customizers have run, so this runs on the finished document.
     */
    @Bean
    public OpenApiCustomizer referencePropertyCleanupCustomizer() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }
            for (Schema<?> component : openApi.getComponents().getSchemas().values()) {
                if (component.getProperties() != null) {
                    component.getProperties().replaceAll((name, property) -> property.get$ref() == null
                        ? property
                        : reference(property.get$ref(), property.getDescription()));
                }
            }
        };
    }

    private static boolean isMarkedNullable(AnnotatedType type) {
        return type.getCtxAnnotations() != null
            && Arrays.stream(type.getCtxAnnotations()).anyMatch(Nullable.class::isInstance);
    }

    private void replaceSharedParameters(Operation operation) {
        if (operation.getParameters() == null) {
            return;
        }
        List<Parameter> parameters = operation.getParameters().stream()
            .filter(parameter -> !("header".equals(parameter.getIn()) && ACCEPT_HEADER.equals(parameter.getName())))
            .map(parameter -> PARAMETER_COMPONENTS.containsKey(parameter.getName())
                ? new Parameter().$ref(PARAMETERS_REF + PARAMETER_COMPONENTS.get(parameter.getName()))
                : parameter)
            .toList();
        operation.setParameters(parameters.isEmpty() ? null : new ArrayList<>(parameters));
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

    // ---------------------------------------------------------------- components

    private static Map<String, Parameter> parameterComponents() {
        Map<String, Parameter> parameters = new LinkedHashMap<>();
        parameters.put("ApiVersionHeader", new Parameter()
            .name(API_VERSION_HEADER)
            .in("header")
            .description("The version of the API that should be used for the current call.  "
                + "If missing, the application default version will be used.")
            .required(false)
            .schema(schema("string").maxLength(API_VERSION_MAX_LENGTH).example("1.2.3")));
        parameters.put("CorrelationIdHeader", new Parameter()
            .name(CORRELATION_ID_HEADER)
            .in("header")
            .description("Unique identifier used for tracing a transaction across microservices.")
            .required(false)
            .schema(schema("string").format("uuid").maxLength(CORRELATION_ID_MAX_LENGTH)
                .example("7ea2c7f7-8b46-475d-86f8-7aaaa9e4a35b")));
        parameters.put("PageQuery", new Parameter()
            .name("page")
            .in("query")
            .description("The number of the page (within a dataset) to start with during data retrieval.")
            .required(false)
            .schema(schema("integer", "string").pattern(INTEGER_PATTERN).format("int32")._default(0)));
        parameters.put("PageSizeQuery", new Parameter()
            .name("pageSize")
            .in("query")
            .description("The number of items to return during data retrieval.")
            .required(false)
            .schema(schema("integer", "string").pattern(INTEGER_PATTERN).format("int32")._default(20)));
        return parameters;
    }

    private static Map<String, ApiResponse> responseComponents() {
        Map<String, ApiResponse> responses = new LinkedHashMap<>();
        responses.put("BadRequest", jsonResponse(
            "Bad Request — The server could not understand the request due to invalid syntax or parameters.",
            new JsonSchema().$ref(SCHEMAS_REF + "ProblemDetailsBadRequest")));
        responses.put("Unauthorized", jsonResponse(
            "Unauthorized — Authentication is required or the provided credentials are invalid or expired.",
            schema("string")));
        responses.put("Forbidden", jsonResponse(
            "Forbidden — The authenticated user does not have permission to access the requested resource.",
            schema("string")));
        responses.put("InternalServerError", jsonResponse(
            "Internal Server Error — An unexpected error occurred on the server.",
            new JsonSchema().$ref(SCHEMAS_REF + "ProblemDetails")));
        return responses;
    }

    @SuppressWarnings("rawtypes")
    private static Map<String, Schema> schemaComponents() {
        Map<String, Schema> schemas = new LinkedHashMap<>();
        schemas.put("DataSourceTypes", schema("integer").description("Enums for the available data sources."));
        schemas.put("ProblemDetailsBadRequest", problemDetails(
            "https://problems-registry.smartbear.com/missing-request-parameter",
            "Missing request parameter",
            400,
            "The request is missing an expected query or path parameter.",
            "/api/checkout/session_9281a"));
        schemas.put("ProblemDetails", problemDetails(
            "/errors/AccountNumberNotFound",
            "Insufficient Funds",
            500,
            "Your current balance is $15.00, but the item costs $20.00.",
            "/api/checkout/session_9281a"));
        return schemas;
    }

    /** The RFC 7807 body written by {@link com.pilotapi.exception.ApiExceptionHandler}. */
    private static Schema<?> problemDetails(String type, String title, int status, String detail, String instance) {
        Map<String, Schema> properties = new LinkedHashMap<>();
        properties.put("type", schema("null", "string")
            .description("A URI reference that identifies the problem type. Typically links to documentation.")
            .example(type));
        properties.put("title", schema("null", "string")
            .description("A short, human-readable summary of the problem type. "
                + "It should not change between occurrences of the same error.")
            .example(title));
        properties.put("status", schema("null", "integer", "string")
            .description("The HTTP status code generated by the origin server for this occurrence of the problem.")
            .pattern(INTEGER_PATTERN)
            .format("int32")
            .example(status));
        properties.put("detail", schema("null", "string")
            .description("A human-readable explanation specific to this occurrence of the problem.")
            .example(detail));
        properties.put("instance", schema("null", "string")
            .description("A URI reference that identifies the specific occurrence of the problem "
                + "(often the request path).")
            .example(instance));
        return schema("object")
            .required(List.of("type", "title"))
            .properties(properties)
            .additionalProperties(true);
    }

    private static Schema<Object> schema(String... types) {
        Schema<Object> schema = new JsonSchema();
        schema.setTypes(new LinkedHashSet<>(List.of(types)));
        return schema;
    }

    private static ApiResponse jsonResponse(String description, Schema<?> schema) {
        return new ApiResponse()
            .description(description)
            .content(new Content().addMediaType(JSON, new MediaType().schema(schema)));
    }

    private static Schema<Object> reference(String ref, String description) {
        Schema<Object> reference = new JsonSchema().$ref(ref);
        reference.setDescription(description);
        return reference;
    }

    private static ApiResponse responseRef(String name) {
        return new ApiResponse().$ref(RESPONSES_REF + name);
    }
}

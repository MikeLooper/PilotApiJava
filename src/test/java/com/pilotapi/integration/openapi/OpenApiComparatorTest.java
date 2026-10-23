package com.pilotapi.integration.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pilotapi.integration.openapi.ComplianceFinding.Severity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.pilotapi.integration.openapi.ComplianceFinding.Severity.ERROR;
import static com.pilotapi.integration.openapi.ComplianceFinding.Severity.INFO;
import static com.pilotapi.integration.openapi.ComplianceFinding.Severity.WARN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the severity assigned to each kind of difference, by changing one thing in an
 * otherwise identical copy of a small specification.
 */
class OpenApiComparatorTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String OPERATION = "/paths/~1items~1{id}/post";
    private static final String ITEM = "/components/schemas/Item";

    private static final String BASELINE = """
        {
          "openapi": "3.1.0",
          "info": {"title": "Pilot", "version": "1.0.0"},
          "servers": [{"url": "http://localhost"}],
          "tags": [{"name": "Items", "description": "Item records"}],
          "paths": {
            "/items/{id}": {
              "post": {
                "tags": ["Items"],
                "summary": "Adds an item.",
                "operationId": "addItem",
                "parameters": [
                  {"name": "id", "in": "path", "required": true, "description": "The id.",
                   "schema": {"type": "integer"}},
                  {"name": "q", "in": "query", "required": false, "schema": {"type": "string"}}
                ],
                "requestBody": {
                  "required": true,
                  "content": {"application/json": {"schema": {"$ref": "#/components/schemas/Item"}}}
                },
                "responses": {
                  "201": {"description": "Created",
                          "content": {"application/json": {"schema": {"$ref": "#/components/schemas/Item"}}}},
                  "500": {"description": "Error"}
                },
                "security": [{"Bearer": []}]
              }
            }
          },
          "components": {
            "schemas": {
              "Item": {
                "type": "object",
                "required": ["name"],
                "properties": {
                  "name": {"type": ["null", "string"], "description": "The name.", "example": "Chai"},
                  "count": {"type": ["integer", "string"], "format": "int32", "pattern": "^\\\\d+$", "default": 0},
                  "kind": {"type": "string", "enum": ["a", "b"]},
                  "child": {"oneOf": [{"type": "null"}, {"$ref": "#/components/schemas/Child"}]}
                }
              },
              "Child": {"type": "object", "properties": {"value": {"type": "string"}}}
            },
            "securitySchemes": {
              "Bearer": {"type": "http", "scheme": "bearer", "bearerFormat": "JWT", "description": "JWT"}
            }
          }
        }
        """;

    static Stream<Arguments> severityCases() {
        return Stream.of(
            // ERROR
            severity(ERROR, "Operation defined in the shared spec is not implemented",
                app -> obj(app, "/paths/~1items~1{id}").remove("post")),
            severity(ERROR, "Required parameter is missing", app -> params(app).remove(0)),
            severity(ERROR, "Parameter required flag differs", app -> ((ObjectNode) params(app).get(1)).put("required", true)),
            severity(ERROR, "Request body is missing", app -> obj(app, OPERATION).remove("requestBody")),
            severity(ERROR, "Request body required flag differs",
                app -> obj(app, OPERATION + "/requestBody").put("required", false)),
            severity(ERROR, "Success response is missing", app -> obj(app, OPERATION + "/responses").remove("201")),
            severity(ERROR, "Schema is missing",
                app -> obj(app, OPERATION + "/responses/201/content/application~1json").remove("schema")),
            severity(ERROR, "Property is missing", app -> obj(app, ITEM + "/properties").remove("count")),
            severity(ERROR, "Base type is incompatible", app -> obj(app, ITEM + "/properties/kind").put("type", "integer")),
            severity(ERROR, "Security requirements differ", app -> obj(app, OPERATION).remove("security")),
            severity(ERROR, "Security scheme definition differs",
                app -> obj(app, "/components/securitySchemes/Bearer").put("bearerFormat", "opaque")),
            severity(ERROR, "Security scheme is defined by the app under a different name", app -> {
                ObjectNode schemes = obj(app, "/components/securitySchemes");
                schemes.set("bearerAuth", schemes.remove("Bearer"));
                ((ObjectNode) obj(app, OPERATION).withArray("security").get(0)).set("bearerAuth",
                    ((ObjectNode) obj(app, OPERATION).withArray("security").get(0)).remove("Bearer"));
            }),
            severity(ERROR, "Media types differ", app -> {
                ObjectNode content = obj(app, OPERATION + "/responses/201/content");
                content.set("*/*", content.remove("application/json"));
            }),
            severity(ERROR, "Nullability differs", app -> obj(app, ITEM + "/properties/name").put("type", "string")),
            severity(ERROR, "Property is required in the shared spec but optional in the app",
                app -> obj(app, ITEM).putArray("required")),
            severity(ERROR, "Property is optional in the shared spec but required in the app",
                app -> obj(app, ITEM).withArray("required").add("kind")),
            severity(ERROR, "Format differs", app -> obj(app, ITEM + "/properties/count").put("format", "int64")),
            severity(ERROR, "Schema pattern differs", app -> obj(app, ITEM + "/properties/count").put("pattern", "^.*$")),
            severity(ERROR, "Schema default differs", app -> obj(app, ITEM + "/properties/count").put("default", 1)),
            severity(ERROR, "Enum values differ",
                app -> obj(app, ITEM + "/properties/kind").putArray("enum").add("a")),
            severity(ERROR, "Composition (anyOf) differs", app -> {
                ArrayNode anyOf = obj(app, "/components/schemas/Child").putArray("anyOf");
                anyOf.addObject().put("type", "object");
                anyOf.addObject().put("type", "object");
            }),
            // WARN
            severity(WARN, "Operation is implemented but not defined in the shared spec",
                app -> obj(app, "/paths").putObject("/other").putObject("get")),
            severity(WARN, "Parameter is not defined in the shared spec",
                app -> params(app).addObject().put("name", "extra").put("in", "query")),
            severity(WARN, "Response is not defined in the shared spec",
                app -> obj(app, OPERATION + "/responses").putObject("404").put("description", "Not Found")),
            severity(WARN, "Property is not defined in the shared spec",
                app -> obj(app, ITEM + "/properties").putObject("extra").put("type", "string")),
            severity(WARN, "Optional parameter is missing", app -> params(app).remove(1)),
            severity(WARN, "Error response is missing", app -> obj(app, OPERATION + "/responses").remove("500")),
            severity(WARN, "Info title differs", app -> obj(app, "/info").put("title", "PilotApiJava")),
            severity(WARN, "OpenAPI version differs", app -> ((ObjectNode) app).put("openapi", "3.0.1")),
            severity(WARN, "Server URLs differ",
                app -> ((ObjectNode) app.get("servers").get(0)).put("url", "http://other")),
            severity(WARN, "Tag is not defined by the app", app -> ((ArrayNode) app.get("tags")).removeAll()),
            severity(WARN, "Operation tags differs", app -> obj(app, OPERATION).putArray("tags").add("Other")),
            severity(WARN, "Operation operationId differs", app -> obj(app, OPERATION).put("operationId", "add_1")),
            // INFO
            severity(INFO, "Schema description differs",
                app -> obj(app, ITEM + "/properties/name").put("description", "Other.")),
            severity(INFO, "Schema example differs", app -> obj(app, ITEM + "/properties/name").put("example", "Tea")),
            severity(INFO, "Parameter description differs",
                app -> ((ObjectNode) params(app).get(0)).put("description", "Other.")),
            severity(INFO, "Operation summary differs", app -> obj(app, OPERATION).put("summary", "Other.")),
            severity(INFO, "Tag description differs",
                app -> ((ObjectNode) app.get("tags").get(0)).put("description", "Other.")),
            severity(INFO, "Alternate types differ",
                app -> obj(app, ITEM + "/properties/count").put("type", "integer")));
    }

    @Test
    void OpenApiComparatorTest_identicalDocuments_produceNoFindings_Test() throws Exception {
        List<ComplianceFinding> findings = new OpenApiComparator(baseline(), baseline()).compare();

        assertTrue(findings.isEmpty(), findings::toString);
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("severityCases")
    void OpenApiComparatorTest_difference_isReportedAtExpectedSeverity_Test(
        Severity expected, String message, Consumer<JsonNode> appChange) throws Exception {
        JsonNode app = baseline();
        appChange.accept(app);

        List<ComplianceFinding> matching = new OpenApiComparator(baseline(), app).compare().stream()
            .filter(finding -> finding.message().equals(message))
            .toList();

        assertFalse(matching.isEmpty(), "Expected a finding: " + message);
        matching.forEach(finding -> assertEquals(expected, finding.severity(), finding::toString));
    }

    @Test
    void OpenApiComparatorTest_renamedComponent_isInfo_Test() throws Exception {
        JsonNode app = MAPPER.readTree(BASELINE.replace("schemas/Child", "schemas/ChildDto")
            .replace("\"Child\":", "\"ChildDto\":"));

        List<ComplianceFinding> findings = new OpenApiComparator(baseline(), app).compare();

        assertEquals(1, findings.size(), findings::toString);
        assertEquals(INFO, findings.get(0).severity());
        assertEquals("Referenced component schema name differs", findings.get(0).message());
    }

    private static Arguments severity(Severity expected, String message, Consumer<JsonNode> appChange) {
        return Arguments.of(expected, message, appChange);
    }

    private static JsonNode baseline() throws Exception {
        return MAPPER.readTree(BASELINE);
    }

    private static ObjectNode obj(JsonNode root, String pointer) {
        return (ObjectNode) root.at(pointer);
    }

    private static ArrayNode params(JsonNode root) {
        return (ArrayNode) root.at(OPERATION + "/parameters");
    }
}

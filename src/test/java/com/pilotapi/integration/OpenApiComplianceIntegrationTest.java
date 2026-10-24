package com.pilotapi.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pilotapi.integration.openapi.ComplianceFinding;
import com.pilotapi.integration.openapi.ComplianceFinding.Category;
import com.pilotapi.integration.openapi.ComplianceFinding.Severity;
import com.pilotapi.integration.openapi.ComplianceReportWriter;
import com.pilotapi.integration.openapi.OpenApiComparator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Boots the full application against an in-memory database and compares the
 * OpenAPI document it publishes with the shared contract in the
 * {@code PilotSharedSource} submodule.
 *
 * <p>Severity rules:
 * <ul>
 *   <li>ERROR (fails the test): spec operation not implemented; required parameter or request body missing
 *       or its required flag differs; 2xx response missing; schema or property missing; base type mismatch;
 *       any security mismatch; media type, nullability, required-property, format, pattern, default, enum
 *       or composition (oneOf/anyOf/allOf) differences.</li>
 *   <li>WARN: extra operation, parameter, request body, response or property; optional parameter or
 *       4xx/5xx response missing; document metadata (openapi version, info, servers, tags, operationIds).</li>
 *   <li>INFO: descriptions and summaries, examples, alternate types (e.g. [integer, string] vs integer),
 *       and component schema names.</li>
 * </ul>
 * Every finding is logged and written to {@code target/openapi-compliance-report.md}.
 */
@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:openapi-compliance;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "otel.sdk.disabled=true"
})
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenApiComplianceIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiComplianceIntegrationTest.class);
    private static final Path SHARED_SPEC_PATH = Path.of("shared", "PilotSharedSource", "OpenAPI", "PilotApi_v1.yaml");
    private static final Path REPORT_PATH = Path.of("target", "openapi-compliance-report.md");

    @Autowired
    private MockMvc mockMvc;

    private List<ComplianceFinding> findings;

    @BeforeAll
    void compareSpecifications() throws Exception {
        assertTrue(Files.isRegularFile(SHARED_SPEC_PATH), "Shared OpenAPI specification not found at "
            + SHARED_SPEC_PATH.toAbsolutePath() + ". Run 'git submodule update --init' to fetch PilotSharedSource.");
        JsonNode sharedSpec = new ObjectMapper(new YAMLFactory()).readTree(SHARED_SPEC_PATH.toFile());

        String appSpecJson = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode appSpec = new ObjectMapper().readTree(appSpecJson);

        findings = new OpenApiComparator(sharedSpec, appSpec).compare();

        ComplianceReportWriter.write(REPORT_PATH, SHARED_SPEC_PATH, findings);
        logFindings();
    }

    @Test
    void OpenApiComplianceIntegrationTest_paths_allSpecOperationsAreImplemented_Test() {
        assertNoErrors(Category.PATHS);
    }

    @Test
    void OpenApiComplianceIntegrationTest_parameters_matchSpec_Test() {
        assertNoErrors(Category.PARAMETERS);
    }

    @Test
    void OpenApiComplianceIntegrationTest_requestBodies_matchSpec_Test() {
        assertNoErrors(Category.REQUEST_BODIES);
    }

    @Test
    void OpenApiComplianceIntegrationTest_responses_matchSpec_Test() {
        assertNoErrors(Category.RESPONSES);
    }

    @Test
    void OpenApiComplianceIntegrationTest_schemas_matchSpec_Test() {
        assertNoErrors(Category.SCHEMAS);
    }

    @Test
    void OpenApiComplianceIntegrationTest_security_matchesSpec_Test() {
        assertNoErrors(Category.SECURITY);
    }

    private void assertNoErrors(Category category) {
        List<ComplianceFinding> errors = findings.stream()
            .filter(finding -> finding.category() == category && finding.severity() == Severity.ERROR)
            .toList();

        assertTrue(errors.isEmpty(), () -> errors.size() + " OpenAPI compliance error(s) in " + category
            + " (full report: " + REPORT_PATH.toAbsolutePath() + "):\n"
            + errors.stream().map(ComplianceFinding::toString).collect(Collectors.joining("\n")));
    }

    private void logFindings() {
        for (ComplianceFinding finding : findings) {
            switch (finding.severity()) {
                case ERROR -> LOGGER.error("{}", finding);
                case WARN -> LOGGER.warn("{}", finding);
                case INFO -> LOGGER.info("{}", finding);
            }
        }
        LOGGER.info("OpenAPI compliance: {} error(s), {} warning(s), {} info. Report: {}",
            count(Severity.ERROR), count(Severity.WARN), count(Severity.INFO), REPORT_PATH.toAbsolutePath());
    }

    private long count(Severity severity) {
        return findings.stream().filter(finding -> finding.severity() == severity).count();
    }
}

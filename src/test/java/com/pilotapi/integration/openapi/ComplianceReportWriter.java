package com.pilotapi.integration.openapi;

import com.pilotapi.integration.openapi.ComplianceFinding.Severity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes OpenAPI compliance findings as a Markdown report, grouped by severity.
 */
public final class ComplianceReportWriter {

    private static final int MAX_VALUE_LENGTH = 160;

    private ComplianceReportWriter() {
    }

    public static void write(Path reportPath, Path specPath, List<ComplianceFinding> findings) throws IOException {
        StringBuilder report = new StringBuilder()
            .append("# OpenAPI Compliance Report\n\n")
            .append("Expected: `").append(specPath.toString().replace('\\', '/')).append("`  \n")
            .append("Actual: application `/v3/api-docs`\n\n")
            .append("| Severity | Count |\n|---|---|\n");
        for (Severity severity : Severity.values()) {
            report.append("| ").append(severity).append(" | ").append(count(findings, severity)).append(" |\n");
        }

        for (Severity severity : Severity.values()) {
            report.append("\n## ").append(severity).append("\n\n");
            List<ComplianceFinding> matching = findings.stream().filter(f -> f.severity() == severity).toList();
            if (matching.isEmpty()) {
                report.append("None.\n");
                continue;
            }
            report.append("| Category | Location | Finding | Expected (shared spec) | Actual (app) |\n")
                .append("|---|---|---|---|---|\n");
            for (ComplianceFinding finding : matching) {
                report.append("| ").append(finding.category())
                    .append(" | ").append(cell(finding.location()))
                    .append(" | ").append(cell(finding.message()))
                    .append(" | ").append(cell(finding.expected()))
                    .append(" | ").append(cell(finding.actual()))
                    .append(" |\n");
            }
        }

        Files.createDirectories(reportPath.toAbsolutePath().getParent());
        Files.writeString(reportPath, report);
    }

    private static long count(List<ComplianceFinding> findings, Severity severity) {
        return findings.stream().filter(f -> f.severity() == severity).count();
    }

    private static String cell(String value) {
        String text = value.replace("\r", " ").replace("\n", " ").replace("|", "\\|");
        return text.length() > MAX_VALUE_LENGTH ? text.substring(0, MAX_VALUE_LENGTH) + "…" : text;
    }
}

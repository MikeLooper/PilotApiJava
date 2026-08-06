# Copilot Instructions: Repository-Wide Guidance

## Purpose
This file contains cross-cutting guidance for the repository.

## Unit Test Instructions
Detailed unit test creation and maintenance rules are defined in `.github/instructions/unit-tests.instructions.md`.
Test resource and fixture rules are defined in `.github/instructions/test-resources.instructions.md`.

When the task is unit-test related, follow that scoped file-instructions document for:
- JUnit 5 and Spring Boot test requirements.
- Test project and directory mapping.
- Coverage, naming, and class conventions.
- Validation workflow and supporting test assets.

When the task involves test resources, fixtures, or test-only data files, follow:
- `.github/instructions/test-resources.instructions.md`.

## Global Guardrails
- Keep instructions scoped to the task at hand.
- Avoid changing unrelated files.
- Preserve existing repository structure and conventions unless the task requires an update.

You are a senior Java and Spring Boot architect. Generate a production-ready Java Spring Boot REST API project from an OpenAPI specification file.

Important constraints:
- Use the OpenAPI specification as the source of truth for API paths, operations, request/response schemas, error models, and validation rules.
- Do not invent or modify endpoints unless explicitly required by the OpenAPI file.
- Keep generated code and architecture aligned with modern Spring Boot best practices.
- Favor maintainability, testability, clear separation of concerns, and clean architecture principles.

Inputs you must use:
- OpenAPI spec location: .\docs\PilotApi_v1.yaml
- Java version: latest
- Spring Boot version: latest
- Build tool: Maven
- Group ID: github.com
- Artifact ID: mikel-looper
- Base package: pilot-api
- Target database: SQL Server
- Auth strategy: None

Primary objective:
Create a complete Spring Boot API application structure whose endpoints and DTO contracts are defined by the OpenAPI spec, with robust internal layering and implementation best practices.

Architecture and structural requirements:
- Organize packages by bounded responsibility with explicit layers:
  - config
  - controller (or api)
  - dto
  - mapper
  - service
  - repository
  - model (entity/domain)
  - exception
  - validation
  - security (if auth is required)
- Keep controller layer thin: only HTTP concerns (status codes, request mapping, validation triggering).
- Implement business logic in service layer.
- Isolate persistence in repository layer.
- Keep mapping between domain models and DTOs in dedicated mappers.
- Use constructor injection everywhere.
- Follow SOLID principles and avoid God classes.

OpenAPI-driven implementation requirements:
- Generate endpoint handlers that match operationIds and paths from the OpenAPI spec.
- Implement request/response DTOs exactly according to the spec schemas.
- Apply field and payload validation consistent with OpenAPI constraints.
- Implement consistent error responses that match spec-defined error models.
- Ensure content types, response codes, and required headers are respected.

Cross-cutting and quality requirements:
- Global exception handling with a standardized error envelope.
- Input validation with clear, client-friendly error messages.
- Logging strategy:
  - structured logs
  - no sensitive data in logs
  - meaningful contextual fields
- Externalized configuration via application properties/yaml and environment variables.
- Health/readiness support via Spring Boot Actuator.
- API documentation exposure aligned with OpenAPI source.
- Use pagination/sorting/filtering patterns where endpoints indicate collection access.

Data and persistence requirements (if persistence is required):
- Use Spring Data JPA (or specified persistence technology).
- Use clear entity modeling and repository abstractions.
- Keep transactional boundaries in service layer.
- Use database migrations (Flyway or Liquibase).

Security requirements (if auth is required):
- Implement stateless authentication/authorization per input strategy.
- Enforce endpoint-level authorization rules.
- Validate and sanitize security-sensitive inputs.
- Use secure defaults (CORS, headers, CSRF strategy appropriate for stateless APIs).

Testing requirements:
- Unit tests for service logic.
- Controller tests for request/response and validation behavior.
- Integration tests for API and persistence slices where appropriate.
- Cover success, validation failures, not-found, conflict, and unexpected-error scenarios.

Non-functional requirements:
- Clear README with setup, run, test, and environment configuration steps.
- Deterministic build and runnable local profile.
- Idiomatic naming conventions and consistent formatting.
- No dead code, placeholders, or TODO stubs in core paths.

Generation process you must follow:
1. Parse and summarize the OpenAPI specification.
2. Produce the proposed package/module structure before code generation.
3. Map OpenAPI operations to controller interfaces/classes and service methods.
4. Map schemas to DTOs and domain models, documenting transformation boundaries.
5. Define exception and error-response strategy.
6. Define repository contracts and transactional service flows.
7. Define test strategy per endpoint and critical business behavior.
8. Generate the project files accordingly.

Output format required from you:
- Start with a concise architecture summary.
- Then provide the full project tree.
- Then provide all generated files with complete content.
- Then provide run/test instructions.
- Then provide a brief checklist showing how implementation satisfies OpenAPI and architecture requirements.

Guardrails:
- Do not skip layers for convenience.
- Do not place business logic in controllers.
- Do not couple persistence entities directly to external API contracts unless explicitly justified.
- Do not diverge from OpenAPI contract.
- Do not output pseudo-code where concrete implementation is expected.

If any requirement conflicts with the OpenAPI spec, prioritize the OpenAPI contract and explicitly document the conflict and chosen resolution.
# Add Security

Implement authentication and authorization on all domain endpoints of PilotApiJava, excluding the System endpoints (`/healthcheck`, `/about`).

## 1. Requirements (Source Specification)

### Cross-Cutting

Use the standard security patterns and practices for Java/Spring Boot (Spring Security + OAuth2 Resource Server).

### Authentication

Use an OIDC-compatible IDP (Keycloak, self-hosted on local Docker), without ever naming the vendor in code/config.
- URLs:
  - Production: `http://local-keycloak:8080`
  - Development: `http://localhost:55001`
- Realm: `local-realm`
- Client ID: `local-client`

An **active flag** controls enforcement:
- Active: failed authentication/authorization blocks access to covered endpoints.
- Inactive: failed authentication/authorization still allows access, but the response carries a `Warning` header describing the failure.

Security settings (URLs, active flag, etc.) live in application configuration. The literal word `Keycloak` must never appear in configuration setting names.

Use JWT and OAuth2.
During token validation, do not check the audience.

### Authorization

Role-based access:

| Role | Description | Endpoint Methods |
| --- | --- | --- |
| `read_only_role` | Can only read data | GET, HEAD, OPTIONS, QUERY, TRACE |
| `read_write_role` | Can read and update data | GET, HEAD, OPTIONS, QUERY, PATCH, POST, PUT, TRACE |
| `admin_role` | All data maintenance | DELETE, GET, HEAD, OPTIONS, QUERY, PATCH, POST, PUT, TRACE |

Users and roles:
- A repository object mocks reading a database table named `UserRoles` (hard-coded for now).
- Mock data:

  | UserId | Role |
  | --- | --- |
  | `reader_user` | `read_only_role` |
  | `working_user` | `read_write_role` |
  | `working_admin_user` | `admin_role` |

- The security token carries user roles, scopes, and client attributes.
- Token custom claims: `realm_access.roles` (default roles array) and `resource_access.<area>.roles` (area-specific roles array, e.g. area `account`).
- The context User must be enriched so auth/authz code can see role, claim, and scope data from the token.

### Centralization

All new security logic is centralized in a `SecurityHelper` class.

### Additional

- Log all successful authentications and failed attempts.
- Redact passwords if present in any log entry.
- Read the `Authorization` header, extract the JWT, and include it in what is logged.
- Update the README with a usage section showing how to call a secured endpoint.
- The word `Keycloak` must not appear in any class, variable, or configuration name.

### Testing

Unit tests cover all new logic and must be run and confirmed passing.

### Final

The resulting plan is saved as a Markdown file under `docs/` (this file).

---

## 2. Current State (from codebase research)

- Spring Boot 3.5.12, Maven, Java 25, Spring MVC (servlet, not reactive).
- **No security dependencies exist at all** — no Spring Security, no OAuth2, no JWT libraries.
- 8 domain controllers under `com.pilotapi.controller.v1`, all `/v1/<resource>`, all with the identical method shape:
  - `GET /get-all`, `GET /get/{id}`, `POST /add`, `PUT /update`, `DELETE /delete/{id}`.
  - This means authorization only needs to key off **HTTP method**, not per-endpoint rules — a single method→role mapping covers all 40 domain endpoints uniformly.
- System endpoints (`GET /healthcheck`, `GET /about`) live in `com.pilotapi.controller.SystemController` at the root path — must stay fully open.
- Config pattern to reuse: `ApplicationMetadataProperties` (`@ConfigurationProperties(prefix = "app.metadata")`, registered via `@EnableConfigurationProperties` in `ApplicationConfig`) and `${VAR:default}` placeholders in `application*.yml`.
- Exception handling: `ApiExceptionHandler` (`@RestControllerAdvice`) translates exceptions to `ProblemDetailsDto` (RFC 7807-style). New 401/403 responses should follow this same shape.
- Logging: SLF4J/Logback via Spring Boot defaults; only existing usage is `LoggerFactory.getLogger(Class)` in `ApiExceptionHandler`.
- Tests: JUnit 5 + Mockito, `@WebMvcTest`/`MockMvc` for controllers, `ApplicationContextRunner` for config classes, naming `<Class>Test.java` / `<Controller>WebMvcTest.java`, package-mirrored under `src/test/java`. Test-only helpers go under `src/test/java/com/pilotapi/testing/...`.

---

## 3. Architecture Overview

Rather than relying on Spring Security's declarative `authorizeHttpRequests().hasAuthority(...)`, enforcement is done in **one custom servlet filter** that defers to `SecurityHelper` for all decisions. This is necessary because the *active flag* requires downgrading a 401/403 into "log + `Warning` header + allow" — behavior that doesn't map cleanly onto Spring Security's normal filter-chain short-circuiting.

Spring Security is still used for the *standard* parts (JWT signature/issuer/expiry validation via `JwtDecoder`, and populating `SecurityContextHolder` with a real `Authentication`), satisfying the "standard patterns and practices" requirement, while the conditional enforce/warn behavior and role logic is custom and centralized.

Request flow for any `/v1/**` request:

1. `AuthEnforcementFilter` (a `OncePerRequestFilter`) intercepts the request. Requests outside `/v1/**` (System endpoints, actuator, Swagger UI) pass straight through untouched.
2. It asks `SecurityHelper` to extract and validate the bearer token (`Authorization: Bearer <jwt>`), using a `JwtDecoder` bean for signature/issuer/expiry validation.
3. On a valid token, `SecurityHelper` resolves the user id from the token, enriches an `EnrichedUser` context object with token roles/scopes/client attributes, looks up the application role via `UserRolesRepository` (the mocked `UserRoles` table), and sets an `Authentication` (principal = `EnrichedUser`) into `SecurityContextHolder`.
4. `SecurityHelper` computes the role required for the request's HTTP method and compares it against the resolved role's rank.
5. `SecurityHelper` logs the outcome (success, or failure with reason) — always redacting passwords and including the raw JWT.
6. If the outcome is a failure (no/invalid token, unknown user, insufficient role):
   - **Active = true** → the filter writes a `ProblemDetailsDto` 401 (not authenticated) or 403 (authenticated, insufficient role) response and stops the chain.
   - **Active = false** → the filter adds a `Warning` response header describing the failure and continues the chain, allowing the request through.
7. If the outcome is success, the filter simply continues the chain.

---

## 4. Configuration Design

New `@ConfigurationProperties(prefix = "app.security")` class `SecurityProperties`, registered in `ApplicationConfig` alongside the existing `ApplicationMetadataProperties`. No property key, class, or variable name may contain the word "Keycloak" — vendor-neutral naming (`identityProviderUrl`, `providerUrl`, etc.) is used throughout; the word only appears in prose (comments/README/this doc).

| Property key | Purpose | `application.yml` (base/dev default) | prod profile override |
| --- | --- | --- | --- |
| `app.security.active` | Enforce vs. warn-only | `${APP_SECURITY_ACTIVE:true}` | same, env-overridable |
| `app.security.provider-url` | IDP base URL | `${APP_SECURITY_PROVIDER_URL:http://localhost:55001}` | `${APP_SECURITY_PROVIDER_URL:http://local-keycloak:8080}` |
| `app.security.realm` | IDP realm | `${APP_SECURITY_REALM:local-realm}` | same |
| `app.security.client-id` | Expected token audience/azp | `${APP_SECURITY_CLIENT_ID:local-client}` | same |
| `app.security.resource-area` | Which `resource_access.<area>.roles` to read | `${APP_SECURITY_RESOURCE_AREA:account}` | same |

Derived issuer URI (standard OIDC/Keycloak path convention): `{provider-url}/realms/{realm}`. `SecurityProperties` exposes an `issuerUri()` helper; the `JwtDecoder` bean is built from it via `NimbusJwtDecoder.withIssuerLocation(...)`.

Files touched: `application.yml` (base defaults, local dev), `application-prod-sqlserver.yml`, `application-prod-postgres.yml` (provider URL override). Local profiles inherit the base default.

---

## 5. Package & Class Design

New package `com.pilotapi.security`:

| Class | Type | Responsibility |
| --- | --- | --- |
| `SecurityProperties` | `@ConfigurationProperties` | Binds `app.security.*`; exposes `issuerUri()`. |
| `SecurityConfig` | `@Configuration` | Defines `SecurityFilterChain` (permitAll + stateless + CSRF disabled + registers `AuthEnforcementFilter`) and the `JwtDecoder` bean. |
| `AuthEnforcementFilter` | `OncePerRequestFilter` | Thin orchestrator: matches `/v1/**`, delegates to `SecurityHelper`, applies active/inactive branching, writes error responses or `Warning` header. |
| `SecurityHelper` | `@Component` | Centralized logic: token extraction/validation, role resolution, method→role authorization check, `EnrichedUser` construction, logging with redaction. |
| `EnrichedUser` | POJO / `Authentication` principal | userId, resolvedRole, token roles (realm + resource), scopes, client attributes, raw claims. |
| `ApplicationRole` | enum | `READ_ONLY_ROLE`, `READ_WRITE_ROLE`, `ADMIN_ROLE`, each with a rank and the set of HTTP methods it satisfies (per the requirements table); unknown/unmapped HTTP methods default to requiring `ADMIN_ROLE` (safe default). |
| `AuthOutcome` | enum/record | Result of a check: `AUTHORIZED`, `UNAUTHENTICATED` (missing/invalid token), `UNKNOWN_USER` (valid token, no `UserRoles` entry), `INSUFFICIENT_ROLE`, each carrying a human-readable reason string. |

New package `com.pilotapi.security.userroles` (mock repository, kept separate from the real `com.pilotapi.repository` JPA repositories since it is explicitly a stand-in, not backed by JPA/the database):

| Class | Type | Responsibility |
| --- | --- | --- |
| `UserRoles` | POJO | `userId`, `role` — mirrors the mock table's shape. |
| `UserRolesRepository` | interface | `Optional<UserRoles> findByUserId(String userId)`. |
| `MockUserRolesRepository` | `@Repository` impl | In-memory `Map<String, UserRoles>` hard-coded with `reader_user`/`working_user`/`working_admin_user` per the spec table. |

Modified existing files:

| File | Change |
| --- | --- |
| `pom.xml` | Add `spring-boot-starter-oauth2-resource-server` (brings Spring Security core + `NimbusJwtDecoder`/`Jwt` classes; the declarative `oauth2ResourceServer()` DSL itself is *not* used — see §3 rationale). |
| `ApplicationConfig.java` | Register `SecurityProperties` in `@EnableConfigurationProperties`. |
| `ApiExceptionHandler.java` | No change required if `AuthEnforcementFilter` writes `ProblemDetailsDto` JSON directly for 401/403 (filters run before `@RestControllerAdvice` is reachable); reuse `ProblemDetailsDto`'s shape for consistency. |
| `application.yml` + prod profile files | Add `app.security.*` block (see §4). |
| `README.md` | New usage section (see §11). |

---

## 6. Authorization Design (Role Mapping)

Roles form a strict hierarchy (higher rank satisfies everything lower ranks satisfy):

| Rank | Role | Satisfies HTTP methods |
| --- | --- | --- |
| 1 | `read_only_role` | GET, HEAD, OPTIONS, QUERY, TRACE |
| 2 | `read_write_role` | rank 1 methods + PATCH, POST, PUT |
| 3 | `admin_role` | rank 2 methods + DELETE |

Required-role-by-method lookup (used regardless of which controller/path is hit, since all 8 domain controllers share the same method shape):

| HTTP method | Minimum required role |
| --- | --- |
| GET, HEAD, OPTIONS, QUERY, TRACE | `read_only_role` |
| POST, PUT, PATCH | `read_write_role` |
| DELETE | `admin_role` |
| anything else (unmapped) | `admin_role` (safe default) |

A request is authorized when `resolvedRole.rank >= requiredRole.rank`.

---

## 7. Authentication Flow Detail

1. `SecurityHelper.extractBearerToken(request)` reads the `Authorization` header, requires a `Bearer ` prefix, returns the raw JWT string or empty.
2. `SecurityHelper.decodeAndValidate(rawToken)` calls the injected `JwtDecoder` (validates signature against the realm's JWKS, issuer, and expiry). Failure → `AuthOutcome.UNAUTHENTICATED` with the decoder's failure reason.
3. `SecurityHelper.resolveUserId(jwt)` reads `preferred_username` (fallback `sub`) as the `UserId` used against the mock `UserRoles` table.
4. `SecurityHelper.buildEnrichedUser(jwt, userRolesEntry)` assembles the `EnrichedUser`: userId, resolved application role (from `UserRolesRepository`; `AuthOutcome.UNKNOWN_USER` if absent), `realm_access.roles`, `resource_access.<resource-area>.roles`, `scope` claim split into a set, and client attributes (`azp`/`client_id`).
5. `SecurityHelper` sets `SecurityContextHolder.getContext().setAuthentication(...)` with `EnrichedUser` as principal and its resolved role (plus raw token roles) as granted authorities — this is the "enrich the context User" requirement, and keeps the design aligned with normal Spring Security idiom even though enforcement itself is custom.
6. `SecurityHelper.authorize(httpMethod, enrichedUser)` compares required vs. resolved role rank → `AuthOutcome.AUTHORIZED` or `AuthOutcome.INSUFFICIENT_ROLE`.

---

## 8. Logging & Redaction

- `SecurityHelper` logs via SLF4J (`LoggerFactory.getLogger(SecurityHelper.class)`), matching the codebase's only existing logging convention.
- Success: `INFO` — userId, resolved role, method, path.
- Failure: `WARN` — reason (`UNAUTHENTICATED` / `UNKNOWN_USER` / `INSUFFICIENT_ROLE`), method, path, whether the request was blocked (active) or allowed (inactive).
- Every log entry includes the raw JWT extracted from the `Authorization` header (per requirement), passed through `SecurityHelper.redact(String)` first.
- `redact(String)` scrubs any `password`/`pwd`/`pass` key-value patterns (JSON or form-encoded, case-insensitive) from the string before it is logged, replacing the value with `***REDACTED***`. Applied defensively to the full log line, not just the JWT, in case any future log call includes request bodies.

---

## 9. Error Responses

- 401 (`UNAUTHENTICATED`) and 403 (`UNKNOWN_USER` / `INSUFFICIENT_ROLE`) are written directly by `AuthEnforcementFilter` as `ProblemDetailsDto` JSON (same shape `ApiExceptionHandler` already produces), since the filter runs ahead of `@RestControllerAdvice`.
- Inactive-mode `Warning` header format (RFC 7234-style): `Warning: 199 pilot-api "<reason>"`, e.g. `Warning: 199 pilot-api "Missing or invalid bearer token"`. The request otherwise proceeds and returns its normal success response.

---

## 10. README Updates

Add a new `## Security Usage` section after "Application Validation" (matching the existing PowerShell `Invoke-RestMethod` example style), covering:
- Obtaining a token from the dev IDP (`http://localhost:55001`, realm `local-realm`, client `local-client`) — example token-request call.
- Calling a secured domain endpoint with `Authorization: Bearer <token>`.
- What a 401/403 `ProblemDetailsDto` body looks like.
- What the `Warning` header looks like when `app.security.active=false`.
- How to toggle `app.security.active` via env var.

---

## 11. Testing Plan

Following existing conventions (JUnit 5, Mockito, `@WebMvcTest`/`ApplicationContextRunner`, `<Class>Test.java` naming, package-mirrored under `src/test/java/com/pilotapi/security/...`):

| Test class | Covers |
| --- | --- |
| `SecurityPropertiesTest` | Binding of `app.security.*`, `issuerUri()` construction, defaults (via `ApplicationContextRunner`, matching `ApplicationMetadataPropertiesTest`). |
| `ApplicationRoleTest` | Rank ordering, method→role mapping including the unmapped-method default. |
| `MockUserRolesRepositoryTest` | All 3 seeded users resolve correctly; unknown user returns empty. |
| `SecurityHelperTest` | Token extraction (missing header, malformed header, valid bearer), decode/validate success & failure paths (mocked `JwtDecoder`), `EnrichedUser` construction from mocked `Jwt` claims, authorize() rank comparisons for every method, redaction of passwords in log strings. |
| `AuthEnforcementFilterTest` | Using mocked `HttpServletRequest`/`HttpServletResponse`/`FilterChain` + mocked `SecurityHelper`: non-`/v1/**` paths pass through untouched; active+failure → chain halted with correct status/body; inactive+failure → chain continues + `Warning` header present; success → chain continues, no warning header. |
| Per-controller `@WebMvcTest` additions (e.g. `CategoriesControllerWebMvcTest`) | Add cases for a secured GET/POST/DELETE demonstrating 200 with a valid role, 403 with insufficient role, using a test-only fake `JwtDecoder`/token-issuing helper under `src/test/java/com/pilotapi/testing/` (no live IDP needed in CI). |

A reusable test helper (`src/test/java/com/pilotapi/testing/TestJwtSupport.java` or similar) builds unsigned/test-signed JWTs and a stub `JwtDecoder` bean for `@WebMvcTest`/filter tests, since CI has no live IDP.

All new/changed tests are run via `mvn test` and must pass before the work is considered complete, per the Testing requirement.

---

## 12. Implementation Task Checklist

1. Add `spring-boot-starter-oauth2-resource-server` to `pom.xml`.
2. Add `app.security.*` config to `application.yml` + prod profile files.
3. Create `SecurityProperties`; register in `ApplicationConfig`.
4. Create `ApplicationRole` enum (rank + method mapping).
5. Create `UserRoles` model, `UserRolesRepository`, `MockUserRolesRepository`.
6. Create `EnrichedUser`, `AuthOutcome`.
7. Create `SecurityHelper` (token extraction/validation, resolution, authorize, logging, redaction).
8. Create `SecurityConfig` (`JwtDecoder` bean, `SecurityFilterChain`: permitAll, stateless, CSRF disabled).
9. Create `AuthEnforcementFilter`; register it in `SecurityConfig`.
10. Add README `## Security Usage` section.
11. Add `src/test/java/com/pilotapi/testing/TestJwtSupport.java` test helper.
12. Write all unit tests from §11.
13. Add secured-endpoint test cases to existing `@WebMvcTest` controller test classes.
14. Run `mvn test`; fix any failures; confirm full pass.

---

## 13. Assumptions & Open Questions

- The mock `UserRoles` table is the **authoritative** source for the application role used in authorization decisions; token role claims (`realm_access.roles`, `resource_access.<area>.roles`) are captured onto `EnrichedUser` for visibility/logging/future use but are not the primary authorization gate. Flag if the intent was instead for token roles to directly drive access.
- `resource_access.<area>.roles` area name defaults to `account` per the example in the spec, made configurable via `app.security.resource-area`.
- No live IDP is assumed available in CI/unit tests; a test-only fake `JwtDecoder` is used instead of hitting a real Keycloak instance. Manual/integration verification against the real dev IDP (`http://localhost:55001`) is a separate manual step, not part of the automated test suite.
- OPTIONS/HEAD/TRACE/QUERY aren't explicitly implemented by any controller today; the role mapping still accounts for them defensively in case the servlet container/Spring MVC surfaces them.
- Swagger UI (`springdoc`) and actuator endpoints are left unsecured (out of scope — only System endpoints are explicitly named as exempt, but actuator/Swagger are already scoped down separately and aren't domain endpoints).

## 14. Out of Scope

- Any change to actual business logic/services/entities.
- A real (non-mocked) `UserRoles` database table/migration.
- Running a live IDP instance as part of this work (Docker/Keycloak setup itself is assumed to already exist per the URLs given).
- Updating the `test/Bruno/` manual API collection (could be a useful follow-up, not required by the spec).

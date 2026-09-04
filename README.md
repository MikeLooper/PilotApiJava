# PilotApiJava

A proof of concept API to explore best-practices and new ideas (Java/Spring Boot)

## Execute

To execute the API, a run command (shown below) would be executed with the desired profile name.

The available profiles can be found in the *Database Profiles* section.

Two options for executing the API are: Powershell or within Visual Studio Code.

### Powershell

Open PowerShell in the project folder:

```
C:/Working/Storage/Dev/GitHub/PilotApiJava
```

Run the API with the desired profile:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=<profile name>"
```

Wait for startup confirmation in logs:

```
Started PilotApiApplication
```

### Visual Studio Code

Open the folder in VS Code:

```
C:/Working/Storage/Dev/GitHub/PilotApiJava
```

Open the integrated terminal: Terminal > New Terminal

Run the API with the desired profile (PowerShell-safe):

```
mvn spring-boot:run "-Dspring-boot.run.profiles=<profile name>"

```

Wait for startup log text:

```
Started PilotApiApplication
```

## Application Validation

Verify the application is running and connected to the database.

Verify the healthcheck endpoint:

```
Invoke-RestMethod -Method Get -Uri "http://localhost:59999/healthcheck"
```

Verify a database-backed endpoint:

```
Invoke-RestMethod -Uri "http://localhost:59999/v1/categories/get/1"
```

View the Swagger UI:

```
Invoke-RestMethod -Uri "http://localhost:59999/swagger-ui/index.html"
```

## Security Usage

All `/v1/**` domain endpoints require a bearer token (JWT/OAuth2) issued by the configured identity provider. The System endpoints (`/healthcheck`, `/about`) are never secured.

### Configuration

| Property | Env override | Default (dev) |
|---|---|---|
| `app.security.active` | `APP_SECURITY_ACTIVE` | `true` |
| `app.security.provider-url` | `APP_SECURITY_PROVIDER_URL` | `http://localhost:55001` |
| `app.security.realm` | `APP_SECURITY_REALM` | `local-realm` |
| `app.security.client-id` | `APP_SECURITY_CLIENT_ID` | `local-client` |
| `app.security.resource-area` | `APP_SECURITY_RESOURCE_AREA` | `account` |

When `app.security.active` is `true`, a missing/invalid token or an insufficient role blocks the request (`401`/`403`). When `false`, the same failures are logged and the request is still allowed through, with a `Warning` response header describing what failed.

### Roles

| Role | Allowed methods |
|---|---|
| `read_only_role` | GET, HEAD, OPTIONS, QUERY, TRACE |
| `read_write_role` | + PATCH, POST, PUT |
| `admin_role` | + DELETE |

Role assignment is currently mocked (`UserRoles`), keyed by the token's `preferred_username`/`sub` claim: `reader_user` → `read_only_role`, `working_user` → `read_write_role`, `working_admin_user` → `admin_role`.

### Obtaining a Token

Tokens are issued by the identity provider's OIDC token endpoint, at `{app.security.provider-url}/realms/{app.security.realm}/protocol/openid-connect/token` (dev default: `http://localhost:55001/realms/local-realm/protocol/openid-connect/token`).

For local/dev testing against the mocked users (`reader_user`, `working_user`, `working_admin_user`), request a token with the Resource Owner Password Credentials (direct access) grant — the client in the identity provider must have this grant type enabled:

```powershell
$tokenResponse = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:55001/realms/local-realm/protocol/openid-connect/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
        grant_type    = "password"
        client_id     = "local-client"
        client_secret = "<client-secret>"   # omit if the client is public, not confidential
        username      = "reader_user"
        password      = "<reader_user-password>"
    }

$token = $tokenResponse.access_token
```

### Calling a Secured Endpoint

Pass the retrieved token as a bearer token:

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:59999/v1/categories/get-all" `
    -Headers @{ Authorization = "Bearer $token" }
```

A missing/invalid token (when `app.security.active=true`) returns:

```json
{
  "type": "about:blank",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Missing or malformed Authorization header",
  "instance": "/v1/categories/get-all"
}
```

An authenticated request with an insufficient role (e.g. `reader_user` calling `DELETE`) returns a `403` with the same shape, `title: "Forbidden"`.

When `app.security.active=false`, a failed check instead returns the normal successful response with an added header, e.g.:

```
Warning: 199 pilot-api "Missing or malformed Authorization header"
```

## OpenTelemetry

The application exports traces, metrics, and logs via OpenTelemetry (OTEL), using the [`opentelemetry-spring-boot-starter`](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/spring/spring-boot-autoconfigure) for auto-instrumentation (Spring MVC, JDBC, etc.) plus an OTLP Logback appender for log export. No Java agent is required.

Telemetry is sent over OTLP/gRPC to a local OpenTelemetry Collector, which routes it to a Grafana LGTM stack (Tempo for traces, Mimir for metrics, Loki for logs):

```
PilotApiJava --OTLP/gRPC--> otel-collector --> Tempo / Mimir / Loki --> Grafana
```

View traces, metrics, and logs in Grafana at `http://localhost:3000`.

### Configuration

| Property | Env override | Default (dev) |
|---|---|---|
| `otel.sdk.disabled` | `OTEL_SDK_DISABLED` | `false` |
| `otel.service.name` | — | `${spring.application.name}` |
| `otel.resource.attributes.deployment.environment` | `OTEL_DEPLOYMENT_ENVIRONMENT` | `development` |
| `otel.exporter.otlp.endpoint` | `OTEL_EXPORTER_OTLP_ENDPOINT` | `http://localhost:4317` (dev profiles) / `http://otel-collector:4317` (base/prod) |
| `otel.exporter.otlp.protocol` | `OTEL_EXPORTER_OTLP_PROTOCOL` | `grpc` |

The base `application.yml` defaults to the container hostname `otel-collector`, matching the pattern used for the database and identity provider. The `local-sqlserver`/`local-postgres` dev profiles override this to `localhost`, since the API runs on the host while the collector runs in Docker. The `prod-sqlserver`/`prod-postgres` profiles override `deployment.environment` to `production`.

To disable telemetry export entirely (e.g. when the collector isn't running), set `OTEL_SDK_DISABLED=true`.

### Querying Logs in Grafana

Application logs land in Loki. In Grafana, open **Explore**, select the **Loki** datasource, and query using [LogQL](https://grafana.com/docs/loki/latest/query/), filtering on these labels:

| Label | Example value | Purpose |
|---|---|---|
| `service_name` | `PilotApiJava` | Scope to this API |
| `deployment_environment` | `development` / `production` | Filter by environment (from `OTEL_DEPLOYMENT_ENVIRONMENT`) |
| `detected_level` / `severity_text` | `info`, `warn`, `error` | Filter by log level |
| `code_namespace` | `com.pilotapi.security.SecurityHelper` | Filter by originating Java class |
| `host_name` | *(machine name)* | Filter by the host the API ran on |

Example queries:

```logql
# All PilotApiJava logs
{service_name="PilotApiJava"}

# Errors and warnings only
{service_name="PilotApiJava", detected_level=~"error|warn"}

# Logs from a specific class
{service_name="PilotApiJava", code_namespace="com.pilotapi.security.SecurityHelper"}

# Production logs mentioning a specific route
{service_name="PilotApiJava", deployment_environment="production"} |= "/v1/categories"
```

Each log entry also carries `trace_id`/`span_id` (when logged within a traced request), so you can jump from a log line directly to its trace in Tempo.

## Port Configuration

The application runs on port `59999` by default. Override it with the `SERVER_PORT` environment variable:

```powershell
$env:SERVER_PORT = "56601"
mvn spring-boot:run "-Dspring-boot.run.profiles=local-sqlserver"
```

Or pass it as a JVM argument without setting an environment variable:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local-sqlserver" "-Dserver.port=56601"
```

## Database Profiles

The application supports **SQL Server** and **PostgreSQL**. The active database is selected by referencing a Spring environment profile.

| Database   | Environment | Profile to use    |
|------------|-------------|-------------------|
| SQL Server | development | `local-sqlserver` |
| PostgreSQL | development | `local-postgres`  |
| SQL Server | production  | `prod-sqlserver`  |
| PostgreSQL | production  | `prod-postgres`   |

### Switching to SQL Server

Use `sqlserver` for the active profile (development environment shown here):

```
mvn spring-boot:run "-Dspring-boot.run.profiles=local-sqlserver"
```

Default connection (overridable via environment variables):

| Variable      | Default                                                                                                  |
|---------------|----------------------------------------------------------------------------------------------------------|
| `DB_URL`      | `jdbc:sqlserver://localhost:1433;databaseName=NorthWind;encrypt=true;trustServerCertificate=true;connectTimeout=30` |
| `DB_USERNAME` | `DevUser`                                                                                                |
| `DB_PASSWORD` | *(see application.yml)*                                                                                  |
| `DB_SCHEMA`   | `dbo`                                                                                                    |

### Switching to PostgreSQL

Use `postgresql` for the active profile (development environment shown here):

```
mvn spring-boot:run "-Dspring-boot.run.profiles=local-postgres"
```

Default connection (overridable via environment variables):

| Variable      | Default                                  |
|---------------|------------------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5432/northwind` |
| `DB_USERNAME` | `DevUser`                                |
| `DB_PASSWORD` | *(see application.yml)*                  |
| `DB_SCHEMA`   | `public`                                 |

### Overriding Connection Settings

Set any of the environment variables before running to point to a different host, port, or database:

```powershell
$env:DB_URL      = "jdbc:postgresql://localhost:5432/mydb"
$env:DB_USERNAME = "myuser"
$env:DB_PASSWORD = "mypassword"
$env:DB_SCHEMA   = "myschema"
mvn spring-boot:run "-Dspring-boot.run.profiles=local-postgres"
```

## Shared Source Submodule

This repository includes [PilotSharedSource](https://github.com/MikeLooper/PilotSharedSource) as a Git submodule located in the `shared/` directory.

### Initial Clone

When cloning this repository for the first time, initialize and populate the submodule with:

```powershell
git clone --recurse-submodules https://github.com/MikeLooper/PilotApiJava
```

If you already cloned without `--recurse-submodules`, run:

```powershell
git submodule update --init --recursive
```

### Updating the Submodule

To pull the latest changes from the upstream submodule repository:

```powershell
git submodule update --remote shared
git add shared
git commit -m "Update shared submodule to latest"
```

## Development

The design of this application was based upon the OpenAPI specification, found in the shared\PilotSharedSource directory (which is a submodule of [PilotSharedSource](https://github.com/MikeLooper/PilotSharedSource)).

### launch.json

Launch settings to simpify startup:
```
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "PilotApiApplication",
            "request": "launch",
            "mainClass": "com.pilotapi.PilotApiApplication",
            "projectName": "mikel-looper",
            "args": ["--spring.profiles.active=local-sqlserver"],
            "console": "integratedTerminal",            
            "serverReadyAction": {
                "action": "openExternally",
                "pattern": "Started PilotApiApplication in|Listening on",
                "uriFormat": "http://localhost:59999/swagger-ui/index.html"
            }
        }
    ]
}
```

## Copilot Instructions

This repository includes scoped Copilot instruction files that guide how AI-assisted changes should be made.

### Instruction Files

- `.github/copilot-instructions.md`
    - Repository-level guidance and pointers to scoped instruction files.
- `.github/instructions/unit-tests.instructions.md`
    - Applies to `src/test/java/**/*.java`.
    - Defines Java/Spring Boot unit-test standards (JUnit 5, Mockito, `@WebMvcTest` conventions, naming, and validation expectations).
- `.github/instructions/test-resources.instructions.md`
    - Applies to `src/test/resources/**`.
    - Defines fixture/resource standards (deterministic data, data hygiene, naming, and validation workflow).

### How Scope Works

Copilot uses each instruction file based on its `applyTo` path pattern.

- Changes in `src/test/java/...` follow the unit-test instructions.
- Changes in `src/test/resources/...` follow the test-resource instructions.
- Other files follow repository-level guidance unless additional scoped instruction files are added.

### How To Interact With These Instructions

Use Copilot Chat with direct, task-oriented prompts.

Examples:

- Ask Copilot to perform work under the current rules:
    - `Add tests for CategoryService and follow the repository unit-test instructions.`
- Ask Copilot to summarize which instructions will apply before editing:
    - `Before you edit, tell me which instruction files apply to this task.`
- Ask Copilot to update instruction files themselves:
    - `Update .github/instructions/unit-tests.instructions.md to require explicit negative-path tests for all service methods.`
- Ask Copilot to propose, then apply:
    - `Propose instruction changes first, then apply them after I confirm.`

### Recommended Workflow

1. Describe the change you want in plain language.
2. Ask Copilot to confirm applicable instruction files.
3. Ask Copilot to implement the change.
4. Ask Copilot to run or report relevant validation (for example, affected test runs).


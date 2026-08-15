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
Invoke-RestMethod -Uri "http://localhost:59999/categories/get/1"
```

View the Swagger UI:

```
Invoke-RestMethod -Uri "http://localhost:59999/swagger-ui/index.html"
```

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


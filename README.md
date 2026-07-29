# PilotApiJava

A proof of concept API to explore best-practices and new ideas (Java/Spring Boot)

# Execute

To execute the API, a run command (shown below) would be executed with the desired profile name.

The available profiles are:
- northwind-localhost
    - Run with the default setting with the SQL Server database.
- postgresql
    - Run with the PostgreSQL database.
- sqlserver
    - Run with the SQL Server database.

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

### Application Validation

Verify the application is running and connected to the database.

Verify the healthcheck endpoint:

```
Invoke-RestMethod -Method Get -Uri "http://localhost:56661/healthcheck"
```

Verify a database-backed endpoint:

```
Invoke-RestMethod -Uri "http://localhost:56661/categories/get/1"
```

## Port Configuration

The application runs on port `56661` by default. Override it with the `SERVER_PORT` environment variable:

```powershell
$env:SERVER_PORT = "8081"
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost"
```

Or pass it as a JVM argument without setting an environment variable:

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost" "-Dserver.port=8081"
```

## Database Configuration

The application supports **SQL Server** and **PostgreSQL**. The active database is selected by including a database Spring profile alongside any environment profile.

| Database   | Profile to add |
|------------|----------------|
| SQL Server | `sqlserver`    |
| PostgreSQL | `postgresql`   |

### Switching to SQL Server

Append `sqlserver` to the active profiles:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost,sqlserver"
```

Default connection (overridable via environment variables):

| Variable      | Default                                                                                                  |
|---------------|----------------------------------------------------------------------------------------------------------|
| `DB_URL`      | `jdbc:sqlserver://local_mssql:1433;databaseName=NorthWind;encrypt=true;trustServerCertificate=true;connectTimeout=30` |
| `DB_USERNAME` | `DevUser`                                                                                                |
| `DB_PASSWORD` | *(see application.yml)*                                                                                  |
| `DB_SCHEMA`   | `dbo`                                                                                                    |

### Switching to PostgreSQL

Append `postgresql` to the active profiles:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost,postgresql"
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
$env:DB_URL      = "jdbc:postgresql://myhost:5432/mydb"
$env:DB_USERNAME = "myuser"
$env:DB_PASSWORD = "mypassword"
$env:DB_SCHEMA   = "myschema"
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost,postgresql"
```

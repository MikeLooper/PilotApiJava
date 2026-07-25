# PilotApiJava

A proof of concept API to explore best-practices and new ideas (Java/Spring Boot)

## Local Northwind Run Alias

Use this command to run the API with the `northwind-localhost` Spring profile:

### Powershell

Open PowerShell in the project folder:

```
C:/Working/Storage/Dev/GitHub/PilotApiJava
```

Run the API with the local profile:

```
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost"
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

Run the API with the local Northwind profile (PowerShell-safe):

```
mvn spring-boot:run "-Dspring-boot.run.profiles=northwind-localhost"

```

Wait for startup log text:

```
Started PilotApiApplication
```

### Application Validation

Verify the application is running and connected to the database.

Verify the healthcheck endpoint:

```
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/healthcheck"
```

Verify a database-backed endpoint:

```
Invoke-RestMethod -Uri "http://localhost:8080/categories/get/1"
```

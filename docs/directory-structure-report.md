# Directory Structure Report

## Overview

This document describes the directory structure created for the **PilotApiJava** project — a Java Spring Boot REST API following established Spring Boot and Maven best practices.

## Actions Taken

A standard Maven project layout was scaffolded with Spring Boot package conventions. No source code was generated; only the directory skeleton was created. Empty directories are tracked by Git using `.gitkeep` placeholder files.

---

## Directory Structure

```
PilotApiJava/
├── docs/                                        # Project documentation
│   └── directory-structure-report.md           # This report
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pilotapi/                   # Root application package
│   │   │       ├── config/                     # Spring configuration classes (beans, security, CORS, etc.)
│   │   │       ├── controller/                 # REST controllers (@RestController)
│   │   │       ├── dto/                        # Data Transfer Objects (request/response payloads)
│   │   │       ├── exception/                  # Custom exception classes and global error handlers
│   │   │       ├── model/                      # Domain/entity classes (@Entity, plain POJOs)
│   │   │       ├── repository/                 # Data access layer (Spring Data repositories)
│   │   │       └── service/                    # Business logic layer (@Service)
│   │   └── resources/
│   │       ├── static/                         # Static web assets (served as-is)
│   │       └── templates/                      # Server-side view templates (Thymeleaf, etc.)
│   └── test/
│       ├── java/
│       │   └── com/pilotapi/
│       │       ├── controller/                 # Integration/unit tests for controllers
│       │       └── service/                    # Unit tests for service classes
│       └── resources/                          # Test-specific configuration and fixtures
├── .gitignore
├── LICENSE
└── README.md
```

---

## Layer Descriptions

| Layer | Package | Responsibility |
|---|---|---|
| **Controller** | `com.pilotapi.controller` | Handles HTTP requests and responses; delegates business logic to services |
| **Service** | `com.pilotapi.service` | Contains business logic; coordinates between controllers and repositories |
| **Repository** | `com.pilotapi.repository` | Provides data access abstractions (e.g., Spring Data JPA interfaces) |
| **Model** | `com.pilotapi.model` | Represents domain entities and data structures |
| **DTO** | `com.pilotapi.dto` | Carries data between layers, decoupling API contracts from domain models |
| **Config** | `com.pilotapi.config` | Centralises Spring beans, security configuration, and application settings |
| **Exception** | `com.pilotapi.exception` | Custom exceptions and a global `@ControllerAdvice` error handler |

---

## Conventions

- **Build tool**: Maven (standard `src/main` / `src/test` layout)
- **Root package**: `com.pilotapi` (derived from the project name *PilotApiJava*)
- **Layered architecture**: Controller → Service → Repository, following separation-of-concerns principles
- **Test mirroring**: The `src/test/java/com/pilotapi/` package mirrors the production package structure so tests are co-located with the code they verify
- **Resources separation**: Production resources live under `src/main/resources`; test-only fixtures and configuration go under `src/test/resources`

---

*Generated: 2026-07-25*

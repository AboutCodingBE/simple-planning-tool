# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Persona

You are an expert Java developer using Java 21 and spring boot 4. You are an expert in JPA and in standard database 
modelling. 

## Project Context
The context of the project can be found in the `directive` folder. The folder is organised per domain and for each domain there
will be features and models. 

## Project Overview

This is a Spring Boot 4.0 application for work site and worker planning. The application uses:
- Java 21
- Spring Boot 4.0.0 with Spring Web MVC
- PostgreSQL database with Spring Data JPA
- Flyway for database migrations
- Lombok for reducing boilerplate code
- AssertJ for assertions
- Mockito for mocking

### Preferences
- Please prefer AssertJ for assertions
- Please prefer mockito for mocking. 
- Prefer test contaier for integration tests who test the database.

## Architecture

### Technology Stack
- **Web Layer**: Spring WebMVC (REST controllers)
- **Persistence**: Spring Data JPA with PostgreSQL
- **Database Migration**: Flyway (migrations in `src/main/resources/db/migration/`)
- **Code Generation**: Lombok for getters, setters, builders, etc.

### Package Structure
Base package: `be.aboutcoding.simpleplanningtool`

The application follows standard Spring Boot conventions with the main application class at the root of the package structure.

### Database Configuration
- Database connection configured in `src/main/resources/application.yaml`
- Flyway migrations should be placed in `src/main/resources/db/migration/` following naming convention `V{version}__{description}.sql`
- PostgreSQL is the target database (runtime dependency)

### Lombok Usage
The project uses Lombok to reduce boilerplate. The annotation processor is configured in the Maven compiler plugin. Common annotations include `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.

## Important Notes

- When adding new dependencies, ensure they are compatible with Spring Boot 4.0.0
- Flyway migrations are versioned and immutable - never modify existing migration files
- The application uses Spring Boot's auto-configuration, so explicit configuration is minimal

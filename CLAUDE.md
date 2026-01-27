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
- Prefer test contaier for integration tests that test the database.

## Coding preferences

- Mapping of a request object to a domain modle should be part of that request object
- Mapping of a domain model to a response object should be part of the reponsne object
- NEVER run tests yourself. I am always in control of running tests.

## Feature Development Workflow

When building a new feature, follow this workflow:

1. **Start with a happy path test**: Write an end-to-end test or integration test that verifies the feature works as intended with normal inputs (no corner cases). This test will fail initially, which is expected.

2. **Implement the feature**: For small features, implement the code needed to make the happy path test pass.

3. **Verify implementation**: The user will run the test to verify if the feature works correctly.
   - If the test fails: Adjust the code until it passes
   - If the test passes: Move to step 4

4. **Add corner case tests**: Write a test for a specific corner case.

5. **Implement corner case handling**: Write the code to handle that corner case.

6. **Iterate**: Repeat steps 4-5 until all corner cases are covered and the feature is fully implemented.

**Important**: Never run tests yourself. The user is always in control of running tests.

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

# Use Case

List all workers that are idle (not assigned to any active site) on a specific date.

The input of this use case:
A date parameter representing the day to check for idle workers.

Input mechanism of this use case:
HTTP GET request to `planning/idle` with the date as a query parameter.

The output of this feature:
A JSON response containing the queried date and a list of idle workers. If no workers are idle on that date, return an empty list.

```json
{
  "date": "2026-01-29",
  "idle_workers": [
    {
      "id": 12345,
      "first_name": "John",
      "last_name": "Doe"
    }
  ]
}
```

# Business Rules

- The date query parameter must be a valid ISO date format (YYYY-MM-DD). If not, return HTTP 400 Bad Request.
- A worker is considered "idle" on a given date if they are NOT assigned to any site where:
    - The site has status `OPEN`
    - AND the date falls within the site's execution period: `executionDate <= date <= executionDate + durationInDays - 1`
- If no workers are idle on the requested date, return an empty `idle_workers` array (not an error).

# Component Overview

## PlanningApi

The REST controller that handles HTTP GET requests for listing idle workers.

The input of this component:
- `date` (String): Query parameter representing the date to check, in ISO format (YYYY-MM-DD)

The output of this component:
- HTTP 200 with `IdleWorkersResponse` JSON body on success
- HTTP 400 Bad Request if the date parameter is invalid or missing

This component depends on:
- IdleWorkerFlowController

## IdleWorkerFlowController

Orchestrates the idle worker lookup flow. Receives a validated date and coordinates the retrieval of idle workers.

The input of this component:
- `date` (LocalDate): The date to check for idle workers

The output of this component:
- `IdleWorkersResponse` containing the date and list of idle workers

This component depends on:
- WorkerRepository

## IdleWorkersResponse

The response model returned by the feature.

IdleWorkersResponse has the following format:

```java
LocalDate date;
List<IdleWorkerDto> idleWorkers;
```

## IdleWorkerDto

The data transfer object representing an idle worker in the response.

IdleWorkerDto has the following format:

```java
Long id;
String firstName;
String lastName;
```

## PlanningRepository

Data access component for querying workers. Should provide a method to find all workers that are not assigned to any OPEN site whose execution period includes the given date.

The input of this component:
- `date` (LocalDate): The date to check

The output of this component:
- `List<Worker>`: All workers not assigned to any OPEN site with an execution period covering the given date

The query logic should find workers where there is NO site assignment satisfying:
- `site.status = 'OPEN'`
- `site.executionDate <= date`
- `date <= site.executionDate + site.durationInDays - 1`

This component depends on:
- Worker entity
- Site entity (for the join query)
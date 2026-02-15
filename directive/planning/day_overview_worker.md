# Use Case

The goal of this use case is to provide an overview of workers and which site they are linked to on a specific date. 

The input of this use case:
- A date in query parameter format

Input mechanism of this use case:
REST API GET request with mapping: `planning/worker/day?date=<a-date>`

The output of this feature:
A day overview containing a list of with all the workers. 
For each worker we see: 
- id
- firstname
- lastname
- current_linked_site
- enddate of the site

Example output format:
```json
{
  "date": "2026-01-23",
  "day_overview": [
    {
      "id": 1234567,
      "firstname": "John",
      "lastname": "Worksalot",
      "current_site": {
        "name": "sitename",
        "until": "end date"
      }
    }
  ]
}
```

# Business Rules

- When a worker has no site linked, the current_site attribute can be null
- Calculate until as: linked site execution_date + (linked site duration_in_days - 1)

# Component Overview

## PlanningApi

This is the REST API controller that handles the incoming GET request. If this component already exists, add the method instead
of creating a new component. 

The input of this component:
- Date parameter from query string (format: query parameter named 'date')

The output of this component:
- HTTP response containing the day overview JSON
- HTTP status 200 for successful requests

This component's responsibilities:
1. Extract the date parameter from the request
2. Validate the date format
3. Delegate to DayPlanningFlowController
4. Transform the result into HTTP response format

This component depends on:
- GetDayOverviewWorkers
- Mapper

## GetDayOverviewWorkers

This component orchestrates the feature execution.

The input of this component:
- date: A validated date object representing the day to query

The output of this component:
A list of WorkerDayDetail objects containing:

```java
WorkerDayDetail {
  Long id;
  String firstname;
  String lastname;
  String linked_site_name;
  LocalDate until;
}
```

This component's responsibilities:
1. Delegate worker and  retrieval to SiteRepository
2. Delegate to PlannedSiteRepository which will call a database function.

This component depends on:
- PlanningRepository


## PlanningRepository

This component handles retrieval of site data from the database. This should be done by a database function that has yet to be
written.

The input of this component:
- date: LocalDate representing the day to query

The output of this component:
- List of WorkerDayDetail which needs to be implemented as a projection because we will work with database function.
- Output is ordered by worker id. 

This component's responsibilities:
1. Query the database for all sites

This component has no internal dependencies (interacts directly with database).

## Mapper

This component transforms the WorkerDayDetail into the output format.

The input of this component:
- A list of WorkerDayDetail


The output of this component:
A WorkerDayOverview object.

This component's responsibilities:
1. Map list of raw planned site results into DayOverview

This component has no dependencies.
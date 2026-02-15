# Use Case

The goal of this use case is to provide an overview of construction sites that are active on a given day, showing which sites are currently ongoing and which workers are assigned to each site.

The input of this use case:
- A date in query parameter format

Input mechanism of this use case:
REST API GET request with mapping: `planning/day?date=<a-date>`

The output of this feature:
A day overview containing a list of planned sites with their details and assigned workers. Each planned site includes:
- Site identification and name
- Execution timeline (start date, duration, end date)
- Status information (days remaining, site status)
- List of workers assigned to the site

Example output format:
```json
{
  "date": "2026-01-23",
  "plannedSites": [
    {
      "site_id": "SITE-001",
      "site_name": "Downtown Office Complex",
      "execution_date": "2026-01-20",
      "duration_in_days": 6,
      "end_date": "2026-01-26",
      "days_remaining": 3,
      "site_status": "OPEN",
      "workers": [
        {
          "worker_id": "W123",
          "worker_firstname": "John",
          "worker_lastname": "Smith"
        },
        {
          "worker_id": "W456",
          "worker_firstname": "Maria",
          "worker_lastname": "Garcia"
        }
      ]
    }
  ]
}
```

# Business Rules

- Only include sites with status 'OPEN'
- Only include sites that have an execution date set
- A site is considered active on the given date if:
    - The execution date equals the given date, OR
    - The given date falls within the time span: execution_date to (execution_date + duration_in_days)
- Calculate end_date as: execution_date + (duration_in_days - 1)
- Calculate days_remaining as: end_date - given_date

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
- DayPlanningFlowController
- DayOverviewMapper

## DayPlanningFlowController

This component orchestrates the day planning overview feature execution.

The input of this component:
- date: A validated date object representing the day to query

The output of this component:
A DayOverview object containing:
```java
DayOverview {
  LocalDate date;
  List<PlannedSite> plannedSites;
}
```

This component's responsibilities:
1. Delegate site retrieval to SiteRepository
2. Delegate to PlannedSiteRepository which will call a database function.

This component depends on:
- SiteRepository
- SiteFilterService
- WorkerRepository
- PlannedSiteMapper

## DayOverview

This is the domain model representing the complete response.

DayOverview has the following format:
```java
LocalDate date;
List<PlannedSite> plannedSites;
```

## PlannedSite

This is the domain model representing a construction site with calculated timeline information.

PlannedSite has the following format:
```java
String site_id;
String site_name;
LocalDate execution_date;
int duration_in_days;
LocalDate end_date;
int days_remaining;
String site_status;
List<Worker> workers;
```

Invariants:
- end_date must equal execution_date + duration_in_days
- days_remaining must equal end_date - query_date

## Worker

This is the domain model representing a worker assigned to a site. In case this domain model already exists in the code,
no need to add an extra.

Worker has the following format:
```java
String worker_id;
String worker_firstname;
String worker_lastname;
```

## PlanningRepository

This component handles retrieval of site data from the database. This will be done by a database function, see
V4__create_get_day_overview_function.sql

The input of this component:
- date: LocalDate representing the day to query

The output of this component:
- List of RawPlannedSites which needs to be implemented as a projection because we will work with database function.

This component's responsibilities:
1. Query the database for all sites
2. Return raw site data for filtering

This component has no internal dependencies (interacts directly with database).

## PlannedSiteMapper

This component transforms the RawPlannedSites and worker data into the output format.

The input of this component:
- A list of RawPlannedSites


The output of this component:
A DayOverview object with all required fields populated, including calculated values:
- end_date (calculated as execution_date + duration_in_days)
- days_remaining (calculated as end_date - queryDate)

This component's responsibilities:
1. Map list of raw planned site results into DayOverview

This component has no dependencies.
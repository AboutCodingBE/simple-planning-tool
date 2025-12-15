# Get planning for

## Use case
In this use case we fetch the planning for certain period of time.
Input:a from date, an until date
Expected results:
A planning object that looks like this: 

```json
{
  "from": "2025-12-8",
  "until": "2025-12-25",
  "weeks": [
    {
      "week": 49,
      "monday": {
        "date": "2026-11-30",
        "sites": []
      },
      "tuesday": {
        "date": "2026-12-01",
        "sites": []
      },
      "wednesday": {
        "date": "2026-12-02",
        "sites": [
          {
            "id":2,
            "name":"Delhaize Waregem",
            "duration_in_days":5,
            "status":"OPEN"
          }
        ]
      },
      "thursday": {
        "date": "2026-12-03",
        "sites": []
      },
      "friday": {
        "date": "2026-12-04",
        "sites": []
      },
      "saturday": {
        "date": "2026-12-05",
        "sites": []
      },
      "sunday": {
        "date": "2026-12-06",
        "sites": []
      }
    }
  ]
}
```

## Business rules

- There is always a planning object filled with the days. 
- sites can be empty if nothing is planned
- the until date cannot be before the from date
- if one of the date inputs is an invalid date, the requests returns a 400 - bad request
- the parameters are not mandatory. If no parameters given, the default is: from - today and until = from + 30 days. 
- if no 'until' date is given, the until date is the from date + 30 days. 
- A full week is always returned. If for example the from date is wednesday 2025-12-17, the actual result will start the week from 
monday 2025-12-15 with, including the original from date. If the until date is for example friday 2025-12-26, the result will 
include the next sunday as well, which is 2025-12-28. 

## Component overview

### PlanningApi
This is the Rest controller which will contain the GET endpoint. If the planning api already exists, the GET method will be
added. 
Input:
- a 'from' date as query parameter
- an 'until' date as a query parameter 

Expected outputs: 
A planning data structure: 

```json
{
  "from": "2025-12-8",
  "until": "2025-12-25",
  "weeks": [
    {
      "week": 49,
      "monday": {
        "date": "2026-11-30",
        "sites": []
      },
      "tuesday": {
        "date": "2026-12-01",
        "sites": []
      },
      "wednesday": {
        "date": "2026-12-02",
        "sites": [
          {
            "id":2,
            "name":"Delhaize Waregem",
            "duration_in_days":5,
            "status":"OPEN"
          }
        ]
      },
      "thursday": {
        "date": "2026-12-03",
        "sites": []
      },
      "friday": {
        "date": "2026-12-04",
        "sites": []
      },
      "saturday": {
        "date": "2026-12-05",
        "sites": []
      },
      "sunday": {
        "date": "2026-12-06",
        "sites": []
      }
    }
  ]
}
```
The response will be responsible for mapping the planning model into an appropriate planningResponse. 

Dependencies: 
- GetPlanning

### GetPlanning
GetPlanning is the planning flow controller. This class controls the flow for creating a planning.
Input: 
- LocalDate from
- LocalDate until

Expected outputs:
```java
public record Planning(
        LocalDate from,
        LocalDate until,
        List<Workday> workdays
) {
}

public record Workday(
        LocalDate date,
        Integer week,
        String dayOfWeek,
        List<SiteView> sites
) {
}

public record SiteView(
        Long id,
        String name,
        Integer durationInDays,
        String status
) {
}

```
Dependencies: 
- SiteRepository

### SiteRepository
The SiteRepository will need a method that will fetch sites for which the execution date is equal to or higher than the 'from'
and lower than the 'until' date and which orders by execution_date asc. 

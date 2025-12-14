# Get planning for

## Use case
In this use case we fetch the planning for certain period of time
Input:a from date, an until date
Expected results:
A planning object that looks like this: 

```json
{
  "from": "2025-12-8",
  "until": "2025-12-25",
  "workdays": [
    {
      "date": "2025-12-8",
      "week": "49",
      "day_of_week": "Thursday",
      "sites": [
        {
          "id": 12345654,
          "name": "Delhaize waregem",
          "duration_in_days": "5", 
          "status": "OPEN"
        },
        {
          "id": 887960554,
          "name": "Parking Alfies",
          "duration_in_days": "7",
          "status": "DONE"
        }
      ]
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
  "workdays": [
    {
      "date": "2025-12-8",
      "week": "49",
      "day_of_week": "Thursday",
      "sites": [
        {
          "id": 12345654,
          "name": "Delhaize waregem",
          "duration_in_days": "5",
          "status": "OPEN"
        },
        {
          "id": 887960554,
          "name": "Parking Alfies",
          "duration_in_days": "7",
          "status": "DONE"
        }
      ]
    }
  ]
}
```

Dependencies: 
- GetPlanning

### GetPlanning
GetPlanning is the planning flow controller. This class controls the flow for creating a planning.
Input: 
- LocalDate from
- LocalDate until

Expected outputs:
a planning structure like the one returned by the REST controller

Dependencies: 
- SiteRepository

### SiteRepository
The SiteRepository will need a method that will fetch sites for which the execution date is equal to or higher than the 'from'
and lower than the 'until' date and which orders by execution_date asc. 

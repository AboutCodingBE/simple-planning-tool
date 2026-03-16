# Month overview

## Use case

In this feature, the user can see an overview of the current month and the 2 upcoming months. The user can see which sites
have been planned in these months grouped by week. So for the month of March, 2026, the user can which sites have been 
planned on week 10, week 11, 12, etc.

Input: 
GET request - /planning/monthly
Request param: date (optional)

Expected results:
A monthly overview object that has the following structure:

```json
[
  {
    "month": "March",
    "weeks": [
      {"number":  10, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  11, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  12, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]},
      {"number":  13, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]},
      {"number":  14, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  },
  {
    "month": "April",
    "weeks": [
      {"number":  14, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  15, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  16, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  }
]
```


## Business rules
- The incoming date is optional
- If the date is not passed, we take by default current date/month + 2 months


## Component overview

### PlanningApi
This is the endpoint controller. If this component already exists, we add the new endpoint.

Input:
A date(not required) via request parameter

Expected outputs: 
A list of MonthlyOverview objects containing the data. 

Dependencies: 
- GetMonthlyOverview
- MonthlyOverview

### GetMonthlyOverview
This is a flow controller. This component takes the validated input from input mechanism and encapsulates the feature. It
delegates work to other components. 

Input: 
A date. 

Expected results:
- Returns a list of MonthlyOverview object

Dependencies: 
- MonthlyOverviewResponse
- PlanningRepository
- MonthlyOverviewMapper

### MonthlyOverviewMapper

This component will transform the result of the database into a MonthlyOverviewResponse. 

Input: 
- Output from the PlanningRepository query / function

Output: 
- List of MontlyOverview objects

Dependencies: 
- MontlyOverview

### PlanningRepository

This component will perform the database function. If this component already exists, just add a new method.

### MonthlyOverview

This is the record type that will be returned. It is a record with the following structure: 

```json
{
    "month": "April",
    "weeks": [
      {"number":  14, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  15, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  16, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  }
```

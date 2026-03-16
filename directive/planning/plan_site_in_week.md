# Plan site in weeek

## Use case
In this feature, a user can plan a site in a specific week of a month
Input:
PUT request - /planning/monthly
PlanSiteForWeekRequest:
```json
{
  "week": 10,
  "year": 2026,
  "site_id": 112
}
```

Expected results:
A link between site and week is persisted in the database. Status 201 is returned on success.

## Business rules
- A request object is mandatory. If no request object, return 400 - Bad Request
- Week is a mandatory field. If not week specified, return 400 - Bad Request
- Year is a mandatory field. If a year is not specified, return a 400 - Bad Request
- site id a mandatory field. If no site is specifid, return 400 - Bad Request
- Site id needs to be of an existing site. If site does not exist, return 404 - Not found 
- The combination of week and year cannot be in the past. It can only be the current-week year or later. 

## Component overview

### PlanningApi
This is the endpoint controller. If this component already exists, we add the new endpoint.

Input:
A PlanSiteForWeekRequest

Expected outputs:
Status 201 on successful request
A link between site and week is persisted in the database

Dependencies:
- PlanSiteForWeekRequest
- PlanSiteForWeek

### PlanSiteForWeek
This component is a flow controller. It takes validated input and then encapsulates the feature, delegating work to 
specific components if necessary. This component is transactional. 

Input: 
- A week number
- A year
- A site id

Output:
- void

Dependencies: 
- PlanningRepository
- SiteRepository
- Site domain entity

### PlannningRepository
This component will store the link between a week and a site in the database. If it already exists, a new method is added

### SiteRepository
This component will fetch a site by id. If this method already exists, no action is needed. 

## Database operations
We need a new table that holds the link between weeks of the year and sites. 
Columns: 
- id (primary key, bigint)
- year (integer)
- week (integer)
- site_id (foreign key linking to site)

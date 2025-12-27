# Get all open sites without execution date

## Use case
In this feature, we fetch all sites with a status equal to 'open' and an planned date equal to null
Input: There is no input
Expected results:
- all sites with status set to open and planned date equal to null
- empty list if there are no sites with status open and planned date equal to null

If there are open sites, the result should look like this:
```json
[
  {
    "id": 1234456,
    "name": "site name",
    "customer_name": "customer name",
    "duration_in_days": 4,
    "desired_date": "2026-01-10",
    "planned_date": null
  },
  {
    "id": 8879685,
    "name": "other site name",
    "customer_name": "other customer name",
    "duration_in_days": 13,
    "desired_date": "2026-01-15",
    "planned_date": null
  }
]
```

## Business rules
-Only sites with status set to 'open' and a planned date of null should be returned

## Component overview

### SiteApi
This is the Rest controller which will contain the GET endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class already exists, it will get a new endpoint. This is a GET endpoint for the url `/sites/unplanned`.

Input: no specific input

Expected outputs:
- emtpy list if there are no open sites
  Result when there are open sites:

```json
[
  {
    "id": 1234456,
    "name": "site name",
    "customer_name": "customer name",
    "duration_in_days": 4,
    "desired_date": "2026-01-10",
    "planned_date": null
  },
  {
    "id": 8879685,
    "name": "other site name",
    "customer_name": "other customer name",
    "duration_in_days": 13,
    "desired_date": "2026-01-15",
    "planned_date": null
  }
]
```

Dependencies: SiteRepository

### SiteRepository

This is a repository which will fetch all sites with status 'open'. If the repository exists already, it will get a new method.
Input: none
Output: a list of Site domain models
dependencies: depends on the `Site` domain model. 

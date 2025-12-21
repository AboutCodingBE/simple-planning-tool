# get all open sites

## Use case
In this feature, we fetch all sites with a status equal to 'open'
Input: There is no input
Expected results:
- all sites with status set to open
- empty list if there are no sites with status open

If there are open sites, the result should look like this:
```json
[
  {
    "id": 1234456,
    "name": "site name",
    "customer_name": "customer name",
    "duration_in_days": 4,
    "desired_date": "2026-01-10",
    "planned_date": "2026-01-10"
  },
  {
    "id": 8879685,
    "name": "other site name",
    "customer_name": "other customer name",
    "duration_in_days": 13,
    "desired_date": "2026-01-15",
    "planned_date": "2026-01-16"
  }
]
```

## Business rules
-Only sites with status set to 'open' should be returned

## Component overview

### SiteApi
This is the Rest controller which will contain the GET endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class already exists, it will get a new endpoint. This is a GET endpoint.

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
    "planned_date": "2026-01-10"
  },
  {
    "id": 8879685,
    "name": "other site name",
    "customer_name": "other customer name",
    "duration_in_days": 13,
    "desired_date": "2026-01-15",
    "planned_date": "2026-01-16"
  }
]
```


Dependencies: SiteRepository

### SiteRepository

This is a repository which will fetch all sites with status 'open'. If the repository exists already, it will get a new method.
Input: none
Output: a list of Site domain models
dependencies: depends on the `Site` domain model. 

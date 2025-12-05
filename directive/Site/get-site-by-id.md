# Get site information by id

## Use case
In this use case we fetch site information searching by id
Input:a site id passed as a path parameter
Expected results:
- if a site has been found, it returns a response with the following structure:
```json
{
  "id": 123456789
  "name": "SITENAME",
  "customer": {
    "id": 123456789,
    "customer_name": "customer name",
    "is_private_customer": false
  },
  "desired_date": "2026-01-10T07:30:00.000Z",
  "planned_date": "2026-01-10T07:30:00.000Z",
  "creation_date": "2026-01-10T07:30:00.000Z",
  "duration_in_days": 7,
  "workers": [
    {
      "id": 1234567,
      "first_name": "Jane",
      "last_name": "Workhard"
    }
  ]
}
```
If no site can be found with the given id, a 404 - not found is returned. 

## Business rules
- id of the site is mandatory
- return 404 if no site can be found

## Component overview

### SiteApi
This is the Rest controller which will contain the GET endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a GET endpoint. the input is a path parameter
representing the id of the site.

The output is a response entity with the following structure: 
```json
{
  "id": 123456789
  "name": "SITENAME",
  "customer": {
    "id": 123456789,
    "customer_name": "customer name",
    "is_private_customer": false
  },
  "desired_date": "2026-01-10T07:30:00.000Z",
  "planned_date": "2026-01-10T07:30:00.000Z",
  "creation_date": "2026-01-10T07:30:00.000Z",
  "duration_in_days": 7,
  "workers": [
    {
      "id": 1234567,
      "first_name": "Jane",
      "last_name": "Workhard"
    }
  ]
}
```

If no site can be found, return a 404. 

### SiteRepository

This is a repository which will save a newly created site in the database. If this component already exists, it gets
a new method taking in a site as parameter.
The expected result of this method is a domain model. 

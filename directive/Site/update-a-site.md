# Update a new site

## Use case
In this feature, the information of an existing site is updated. 

Input:
The input mechanism is a PUT endpoint. 
There is a path paratemeter representing the id of the site.
There is also a request with the following format: 
```json
{
  "name": "SITENAME",
  "customer_name": "customer name",
  "is_private_customer": true,
  "desired_date": "2026-01-10T07:30:00.000Z",
  "duration_in_days": 7,
  "transport": "Truck and Crane"
}
```
Expected results: 
- if a valid request, the endpoint returns a 200 - ok
- if a valid request comes in, the site with the target id gets updated
- If the request is invalid, a 400 should be returned with a message indicating what is missing
- If there is no site with the given id, a 404 - not found is returned. 


## Business rules
- a name is a mandatory field and cannot be null
- a customer name is a mandatory field and cannot be null
- is_private_customer is a mandatory field and cannot be null
- duration in days is a mandatory field and cannot be null
- transport is mandatory, but the value can be null. 

## Component overview

### SiteApi
This is the Rest controller which will contain the PUT endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a PUT endpoint. The input is a request body:

```json
{
  "name": "SITENAME",
  "customer_name": "customer name",
  "is_private_customer": true,
  "desired_date": "2026-01-10T07:30:00.000Z",
  "duration_in_days": 7,
  "transport": "Truck and Crane"
}
```
The output is a 200 - ok response.
This component depends directly on the `SiteRepository`

### siteRepository
This is a repository which will save a newly created site in the database. If this component already exists, and it doesn't have
a method yet to get a site by id from the database, then this method is added. The site will be updated as a managed object. 
The expected result of this method is that the updated site is persisted. 




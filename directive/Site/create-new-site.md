# Create a new site

## Use case
In this feature, we will add a new site to the database. 

Input: the input is a request with the following format: 
```json
{
  "name": "SITENAME",
  "customer_name": "customer name",
  "is_private_customer": true,
  "desired_date": "2026-01-10T07:30:00.000Z",
  "duration_in_days": 7
}
```
Expected results: 
- if a valid request, the endpoint returns the id of the newly created site
- if a valid request comes in, a new customer and a new site should be persisted in the database
- If the request is invalid, a 400 should be returned with a message indicating what is missing


## Business rules
- a name is a mandatory field
- a customer name is a mandatory field
- a customer is by default NOT a private customer
- duration in days can be null
- desired date can be null

## Component overview

### SiteApi
This is the Rest controller which will contain the POST endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a POST endpoint. The input is a request body:

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
The output is a response entity returning the id of the newly created site.
This component depends directly on the `SiteRepository`

### siteRepository
This is a repository which will save a newly created site in the database. If this component already exists, it gets
a new method taking in a site as parameter.
The expected result of this method is that the site is persisted. 




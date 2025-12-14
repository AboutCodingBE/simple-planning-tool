# Plan a site

## Use case
In this usecase, we set an execution date on a site
Input: a site id and a date
Expected results:
- The given date is set as the exeuction date of target site. 


## Business rules
An execution date cannot be in the past. If that happens, return a 400 - bad request.
A 404 - not found is returned when there is no site with the given id


## Component overview

### PlanningApi
This is the Rest controller which will contain the PATCH endpoint. This endpoint depends on the site domain and the SiteRepository.
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a PATCH endpoint.

Input:
- a site id as path parameter
- a date as query parameter

Expected outputs: 
Status 204 - updated when the request was succesful. 
The target site is updated with the given endpoint. 



# Link worker to site

## Use case
In this use case, we link a worker to a certain site on a certain date. We will use a PATCH request.
Input:a worker id, a site id
Expected results:
A worker is linked to a site.

## Business rules
A worker can only be linked to a site that has an execution date. If no execution date, return a 400 - bad request

## Component overview

### PlanningApi
This is the Rest controller which will contain the PATCH endpoint. In this feature, if this class exists already, it 
will get a new endpoint. This is a PATCH endpoint.

Input:
The site id is a path parameter representing the id of the site. 
The worker id comes in as a query parameter

Expected outputs: 
The worker is linked to the site.

Dependencies: 
The site domain model.
The worker domain model
The SiteRepository
The WorkerRepository

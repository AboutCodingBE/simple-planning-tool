# Unlink worker to site

## Use case
In this use case, we unlink a worker from a certain site. We will use a PATCH request.
Input:a worker id, a site id
Expected results:
A worker is unlinked from the target site.

The worker id will be a request parameter. 
Exmample request: 

```json
PATCH /sites/${siteId}/unlink?workerId=${workerId}
```


## Business rules
return a 404 if the site with the target id is not found. 

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
UnlinkWorker


### UnlinkWorker

This is a Flow Controller. This component will fetch the  site, check if the target worker is linked and then unlink the 
target worker. 

Input: 
The site id and the worker id. 
Expected outputs: 
The worker is unlinked from the site

Dependencies:
The site domain model. 
The SiteRepository
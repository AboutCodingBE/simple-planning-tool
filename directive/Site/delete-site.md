# Deleting a site

## Use case
In this use case, a site and it linked customer can be deleted from the database
Input:an id of a site, as a path parameter
Input Mechanism: a DELETE endpoint
Expected results:
The site and its linked customer if any, is removed from persistence

## Business rules
If a site with the specified id is not found, a 200 is returned
if a customer is linked to the site, that customer is also removed 
if workers are linked to the site, the workers ARE NOT DELETED, only the link between the workers and the site. 

## Component overview

### SiteApi
This is the Rest controller which will contain the DELETE endpoint. This endpoint depends on the domain and the SiteRepository.
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a DELETE endpoint. the input is a path parameter
representing the id of the site.

The output is a response entity returning the id of the newly created site.

### SiteRepository

This is a repository which will save a newly created site in the database. If this component already exists, it gets
a new method taking in a site as parameter.
The expected result of this method is that the site is persisted. 

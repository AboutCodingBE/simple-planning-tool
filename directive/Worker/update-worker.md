# Create a new worker

## Use case
In this use case, we will add a new worker to the database.
The input mechanism is a REST endpoint using the PUT verb. 
This endpoint has a path parameter
The request body looks like this:

```json
{
  "first_name": "a first name",
  "last_name": "a last name"
}
```
The result is that the worker is updated in the database.

## Business rules

Both the first name and the last name are mandatory. 
If either one or both are missing, return a 400 - bad request. 
If the worker with the target id cannot be found, return a 404. 

## Component overview

### WorkerApi
This is the Rest controller which will contain the PUT endpoint. This endpoint depends on the domain and the WorkerRepository. 
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a PUT endpoint. 
This endpoint has a path parameter, which indicates the id of the worker to be updated. 
The input is a request body:

```json
{
  "first_name": "a first name",
  "last_name": "a last name"
}
```

The output is a response entity returning the id of the newly created worker. 

### WorkerRepository
This is a repository which will save a newly created worker in the database. If this component already exists, it gets 
a new method taking in a Worker as parameter. 
The expected result of this method is that the worker is updated. 
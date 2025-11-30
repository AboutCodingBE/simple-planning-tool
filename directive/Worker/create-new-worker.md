# Create a new worker

## Use case
In this use case, we will add a new worker to the database.
The input mechanism is a REST endpoint using the POST verb. 
The request body looks like this:

```json
{
  "first_name": "a first name",
  "last_name": "a last name"
}
```
The result is that the new worker is persisted. The id of the worker is returned. 

## Business rules

Both the first name and the last name are mandatory. 
If either one or both are missing, return a 400 - bad request. 

## Component overview

### WorkerApi
This is the Rest controller which will contain the POST endpoint. This endpoint depends on the domain and the WorkerRepository. 
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a POST endpoint. The input is a request body:

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
The expected result of this method is that the worker is persisted. 
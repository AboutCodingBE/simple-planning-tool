# Create a new worker

## Use case
In this use case, we will add a new worker to the database.
The input mechanism is a REST endpoint using the GET verb. 

The input of this feature is an id of a worker. This will be a path parameter.
The result of this feature is a construct representing a worker: 

```json
{
  "id": 12345678,
  "first_name": "a first name",
  "last_name": "a last name",
  "date_of_creation": "2025-11-30T13:45:000"
}
```

## Business rules

The id of the worker to fetch is mandatory.
Return a 404 if the worker cannot be found. 

## Component overview

### WorkerApi
This is the Rest controller which will contain the GET endpoint. This endpoint depends on the domain and the WorkerRepository. 
In this feature, if this class didn't exist yet, it will get a new endpoint. This is a GET endpoint. The input is the path parameter
which indicates the id of the worker to be fetched

The output is a response entity returning a structure representing a worker: 

```json
{
  "id": 12345678,
  "first_name": "a first name",
  "last_name": "a last name",
  "date_of_creation": "2025-11-30T13:45:000"
}
```

### WorkerRepository
This is a repository which will save a newly created worker in the database. If this component already exists, it gets 
a new method taking in a long representing hte id of the worker to be fetched. 
The expected result of this method is a worker entity / model. 
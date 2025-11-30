# Create a new worker

## Use case
In this use case, we will delete a worker from the system.
The input mechanism is a REST endpoint using the DELETE verb.

The input of this feature is an id of a worker. This will be a path parameter.
The result is that the new worker is deleted.  

## Business rules

The id of the worker to be deleted is mandatory. 
If the worker cannot be found, we don't throw an exception.  

## Component overview

### WorkerApi
This is the Rest controller which will contain the DELETE endpoint. This endpoint depends on the domain and the WorkerRepository. 
In this feature, if this class already exists, it will get a new endpoint. The endpoint is a DELETE endpoint. 
The input is the worker id.

The output is a 200 if the worker was succesfully deleted. 

### WorkerRepository
This is a repository which will delete a worker in the database. If this component already exists, it gets 
a new method taking in a workr id as parameter. 
The expected result of this method is that the worker is deleted. 
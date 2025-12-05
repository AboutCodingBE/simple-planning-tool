## Sites

### Table name: sites

Long id,
text name,
Long customer_id,
timestamp desired_date,
timestamp execution_date,
integer duration_in_days,
text transport,
timestamp creation_date,
String site_status

### Table name: Customers

Long id
String name
boolean is_private


### Domain model

Class Site:

Long id
String Name
Customer Customer
Instant desiredDate
Instant executionDate
Integer durationInDays
String transport
Instant creationDate
SiteStatus status
List<Worker> workers

Class Customer:
Id
Name
isPrivate

Enum SiteStatus:
Open | Done

### Invariants:
A Site needs a name. You can't create a site without a name. 
A site without a customer cannot be planned in. 
Transport can be empty
Workers can be empty
Duration in days needs to be a positive number and < 1000

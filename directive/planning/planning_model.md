## Planning

A planning is a view. It is not an entity. A planning is a view of a period in time where we show all the days in that
period of time. For each day, we show the sites with an execution date on that day. We put a maximum of 90 days for a planning. 

## model

Class: planning

LocalDate from
LocalDate until
List<Workday> workdays

Class: Workday

LocalDate date
Integer week
String dayOfWeek
List<SiteView> sites

Class: SiteView

Long id
String name
Integer durationInDays
String Status


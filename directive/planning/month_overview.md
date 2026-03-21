# Month overview

## Use case

In this feature, the user can see an overview of the current month and the 2 upcoming months. The user can see which sites
have been planned in these months grouped by week. So for the month of March, 2026, the user can which sites have been 
planned on week 10, week 11, 12, etc.

Input: 
GET request - /planning/monthly

Expected results:
A monthly overview object that has the following structure:

```json
[
  {
    "month": "March",
    "weeks": [
      {"number":  10, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  11, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  12, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]},
      {"number":  13, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]},
      {"number":  14, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  },
  {
    "month": "April",
    "weeks": [
      {"number":  14, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  15, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  16, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  }
]
```


## Business rules
- nothing special


## Component overview

### PlanningApi
This is the endpoint controller. If this component already exists, we add the new endpoint.

Input:
A date(not required) via request parameter

Expected outputs: 
A list of MonthlyOverview objects containing the data. 

Dependencies: 
- GetMonthlyOverview
- MonthlyOverview

### GetMonthlyOverview
This is a flow controller. This component takes the validated input from input mechanism and encapsulates the feature. It
delegates work to other components. 

Input: 
A date. 

Expected results:
- Returns a list of MonthlyOverview object

Dependencies: 
- MonthlyOverviewResponse
- PlanningRepository
- MonthlyOverviewMapper

### MonthlyOverviewMapper

This component will transform the result of the database into a MonthlyOverviewResponse. 

Input: 
- Output from the PlanningRepository query / function

Output: 
- List of MontlyOverview objects

Dependencies: 
- MontlyOverview

### PlanningRepository

This component will perform the database function. If this component already exists, just add a new method.

### MonthlyOverview

This is the record type that will be returned. It is a record with the following structure: 

```json
{
    "month": "April",
    "weeks": [
      {"number":  14, "sites":  [{"id": 12, "name": "Delhaize"}, {"id": 23, "name": "Cobix"}]},
      {"number":  15, "sites":  [{"id": 13, "name": "Delhaize"}, {"id": 24, "name": "Cobix"}]},
      {"number":  16, "sites":  [{"id": 14, "name": "Delhaize"}, {"id": 25, "name": "Cobix"}]}
    ]
  }
```

## Database operations

We need a new flyway migration file that will create the following function: 

```sql
CREATE OR REPLACE FUNCTION get_week_planning_overview()
RETURNS TABLE (
    month INT,
    month_name TEXT,
    iso_week INT,
    planned_site_ids BIGINT[],
    planned_site_names TEXT[],
    sites_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        weeks.month,
        weeks.month_name,
        weeks.iso_week,
        COALESCE(ARRAY_AGG(s.id) FILTER (WHERE s.id IS NOT NULL), ARRAY[]::BIGINT[]) AS planned_site_ids,
        COALESCE(ARRAY_AGG(s.name) FILTER (WHERE s.name IS NOT NULL), ARRAY[]::TEXT[]) AS planned_site_names,
        COUNT(swp.site_id) AS sites_count
    FROM (
        SELECT DISTINCT
            EXTRACT(MONTH FROM date_val)::INT AS month,
            TRIM(TO_CHAR(date_val, 'Month')) AS month_name,
            EXTRACT(WEEK FROM date_val)::INT AS iso_week,
            EXTRACT(YEAR FROM date_val)::INT AS year
        FROM generate_series(
            DATE_TRUNC('month', CURRENT_DATE)::DATE,
            (DATE_TRUNC('month', CURRENT_DATE) + INTERVAL '2 months' + INTERVAL '1 month - 1 day')::DATE,
            '1 day'::INTERVAL
        ) AS date_val
    ) weeks
    LEFT JOIN site_week_planning swp ON weeks.iso_week = swp.week 
        AND weeks.year = swp.year
    LEFT JOIN sites s ON swp.site_id = s.id
    GROUP BY weeks.month, weeks.month_name, weeks.iso_week, weeks.year
    ORDER BY weeks.month, weeks.iso_week;
END;
$$ LANGUAGE plpgsql;
```
Usage:
sql-- Simply call it
`SELECT * FROM get_week_planning_overview();`

Please create a projection for the function. 
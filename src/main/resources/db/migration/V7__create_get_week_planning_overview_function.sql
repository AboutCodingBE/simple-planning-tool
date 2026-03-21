CREATE OR REPLACE FUNCTION get_week_planning_overview()
RETURNS TABLE (
    month INT,
    month_name TEXT,
    iso_week INT,
    planned_site_ids BIGINT[],
    planned_site_names VARCHAR[],
    sites_count BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        weeks.month,
        weeks.month_name,
        weeks.iso_week,
        COALESCE(ARRAY_AGG(s.id) FILTER (WHERE s.id IS NOT NULL), ARRAY[]::BIGINT[]) AS planned_site_ids,
        COALESCE(ARRAY_AGG(s.name) FILTER (WHERE s.name IS NOT NULL), ARRAY[]::VARCHAR[]) AS planned_site_names,
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

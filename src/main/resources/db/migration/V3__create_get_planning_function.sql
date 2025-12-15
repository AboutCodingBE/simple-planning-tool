CREATE OR REPLACE FUNCTION get_planning(p_from_date DATE, p_until_date DATE)
RETURNS TABLE (
    workday_date DATE,
    week_number INTEGER,
    day_of_week TEXT,
    site_id BIGINT,
    site_name VARCHAR(255),
    duration_in_days INTEGER,
    site_status VARCHAR(50)
) AS $$
DECLARE
    v_start_date DATE;
    v_end_date DATE;
BEGIN
    -- Get Monday of the week containing p_from_date
    v_start_date := DATE_TRUNC('week', p_from_date)::DATE;

    -- Get Sunday of the week containing p_until_date
    -- (week starts Monday, so add 6 days to get to Sunday)
    v_end_date := (DATE_TRUNC('week', p_until_date) + INTERVAL '6 days')::DATE;

    RETURN QUERY
    SELECT
        d.date_val::DATE AS workday_date,
        EXTRACT(WEEK FROM d.date_val)::INTEGER AS week_number,
        TRIM(TO_CHAR(d.date_val, 'Day')) AS day_of_week,
        s.id AS site_id,
        s.name AS site_name,
        s.duration_in_days,
        s.site_status::VARCHAR AS site_status
    FROM
        generate_series(v_start_date, v_end_date, '1 day'::INTERVAL) AS d(date_val)
    LEFT JOIN
        sites s ON s.execution_date = d.date_val::DATE
    ORDER BY
        d.date_val, s.id;
END;
$$ LANGUAGE plpgsql;
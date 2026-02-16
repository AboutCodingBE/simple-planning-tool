CREATE OR REPLACE FUNCTION get_worker_day_overview(p_date DATE)
RETURNS TABLE (
    worker_id BIGINT,
    worker_first_name VARCHAR(255),
    worker_last_name VARCHAR(255),
    linked_site_name VARCHAR(255),
    site_until DATE
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        w.id AS worker_id,
        w.first_name AS worker_first_name,
        w.last_name AS worker_last_name,
        s.name AS linked_site_name,
        (s.execution_date + s.duration_in_days - 1)::DATE AS site_until
    FROM workers w
    LEFT JOIN site_workers sw ON w.id = sw.worker_id
    LEFT JOIN sites s ON sw.site_id = s.id
        AND s.site_status = 'OPEN'
        AND s.execution_date IS NOT NULL
        AND s.duration_in_days IS NOT NULL
        AND p_date >= s.execution_date
        AND p_date <= (s.execution_date + s.duration_in_days - 1)
    ORDER BY w.id ASC;
END;
$$ LANGUAGE plpgsql;

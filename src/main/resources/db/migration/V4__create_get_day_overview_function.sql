CREATE OR REPLACE FUNCTION get_active_sites_on_date(p_date DATE)
RETURNS TABLE (
    site_id BIGINT,
    site_name VARCHAR(255),
    customer_id BIGINT,
    execution_date DATE,
    duration_in_days INTEGER,
    end_date DATE,
    days_remaining INTEGER,
    site_status VARCHAR(50),
    worker_id BIGINT,
    worker_first_name VARCHAR(255),
    worker_last_name VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        s.id AS site_id,
        s.name AS site_name,
        s.customer_id,
        s.execution_date,
        s.duration_in_days,
        (s.execution_date + s.duration_in_days - 1)::DATE AS end_date,
        (s.execution_date + s.duration_in_days - p_date)::INTEGER AS days_remaining,
        s.site_status,
        w.id AS worker_id,
        w.first_name AS worker_first_name,
        w.last_name AS worker_last_name
    FROM sites s
    LEFT JOIN site_workers sw ON s.id = sw.site_id
    LEFT JOIN workers w ON sw.worker_id = w.id
    WHERE s.execution_date IS NOT NULL
      AND s.duration_in_days IS NOT NULL
      AND s.site_status = 'OPEN'
      AND p_date >= s.execution_date
      AND p_date <= (s.execution_date + s.duration_in_days - 1)
    ORDER BY s.execution_date, s.id, w.last_name;
END;
$$ LANGUAGE plpgsql;
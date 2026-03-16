CREATE TABLE site_week_planning (
    id BIGSERIAL PRIMARY KEY,
    week INTEGER NOT NULL,
    year INTEGER NOT NULL,
    site_id BIGINT NOT NULL,
    CONSTRAINT fk_site FOREIGN KEY (site_id) REFERENCES sites(id)
);

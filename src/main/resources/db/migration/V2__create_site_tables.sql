CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_private BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_id BIGINT,
    desired_date DATE,
    execution_date DATE,
    duration_in_days INTEGER,
    transport VARCHAR(255),
    creation_date TIMESTAMP,
    site_status VARCHAR(50),
    CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE site_workers (
    site_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    PRIMARY KEY (site_id, worker_id),
    CONSTRAINT fk_site FOREIGN KEY (site_id) REFERENCES sites(id),
    CONSTRAINT fk_worker FOREIGN KEY (worker_id) REFERENCES workers(id)
);

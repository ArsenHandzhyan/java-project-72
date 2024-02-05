DROP TABLE IF EXISTS websites;

CREATE TABLE websites (
    id SERIAL PRIMARY KEY,
    make VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL
);

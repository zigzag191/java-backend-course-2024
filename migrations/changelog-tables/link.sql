--liquibase formatted sql

--changeset zigzag191:2
CREATE TABLE link(
    link_id SERIAL PRIMARY KEY,
    url TEXT UNIQUE NOT NULL,
    type TEXT NOT NULL,
    last_polled TIMESTAMP WITH TIME ZONE NOT NULL
);

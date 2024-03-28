--liquibase formatted sql

--changeset zigzag191:2
CREATE TABLE link
(
    link_id     SERIAL PRIMARY KEY,
    url         TEXT UNIQUE              NOT NULL,
    type        TEXT                     NOT NULL,
    last_polled TIMESTAMP WITH TIME ZONE NOT NULL
);

-- changeset zigzag191:add_link_type_constraint
ALTER TABLE link
    ADD CONSTRAINT link_type_constraint CHECK (type IN ('GITHUB_REPOSITORY', 'STACK_OVERFLOW_QUESTION'));

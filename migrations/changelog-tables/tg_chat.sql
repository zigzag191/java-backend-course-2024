--liquibase formatted sql

--changeset zigzag191:1
CREATE TABLE tg_chat(
    chat_id INTEGER PRIMARY KEY
);

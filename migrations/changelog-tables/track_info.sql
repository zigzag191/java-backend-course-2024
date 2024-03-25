--liquibase formatted sql

--changeset zigzag:3
CREATE TABLE track_info(
    link_id INTEGER REFERENCES link (link_id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    tg_chat INTEGER REFERENCES tg_chat (chat_id)
        ON DELETE RESTRICT
        ON UPDATE RESTRICT,

    PRIMARY KEY (link_id, tg_chat)
);

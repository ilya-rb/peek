CREATE TABLE articles
(
    link       TEXT        NOT NULL,
    PRIMARY KEY (link),
    title      TEXT        NOT NULL,
    date       timestamptz NOT NULL,
    tags       TEXT[]      NOT NULL,
    source     TEXT        NOT NULL,
    created_at timestamptz NOT NULL,

    constraint check_link_length check (length(link) <= 500),
    constraint check_title_not_empty check (length(title) > 0),
    constraint check_link_not_empty check (length(link) > 0)
);
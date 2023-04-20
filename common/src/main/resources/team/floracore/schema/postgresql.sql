-- FloraCore PostgreSQL Schema

CREATE TABLE IF NOT EXISTS "{prefix}players"
(
    id         SERIAL                              NOT NULL PRIMARY KEY,
    uuid       VARCHAR(32)                         NOT NULL,
    name       VARCHAR(36)                         NOT NULL,
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

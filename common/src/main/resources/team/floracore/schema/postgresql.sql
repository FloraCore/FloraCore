-- FloraCore PostgreSQL Schema

CREATE TABLE IF NOT EXISTS "{prefix}players"
(
    id             SERIAL                              NOT NULL PRIMARY KEY,
    uuid           VARCHAR(32)                         NOT NULL,
    name           VARCHAR(36)                         NOT NULL,
    firstLoginIp   VARCHAR(39)                         NOT NULL,
    lastLoginIp    VARCHAR(39)                         NOT NULL,
    firstLoginTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastLoginTime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    playTime       BIGINT                              NOT NULL
);

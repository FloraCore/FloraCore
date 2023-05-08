-- FloraCore PostgreSQL Schema

CREATE TABLE IF NOT EXISTS "{prefix}players"
(
    id             SERIAL      NOT NULL PRIMARY KEY,
    uuid           VARCHAR(36) NOT NULL,
    name           VARCHAR(16) NOT NULL,
    firstLoginIp   VARCHAR(39) NOT NULL,
    lastLoginIp    VARCHAR(39) NOT NULL,
    firstLoginTime BIGINT      NOT NULL,
    lastLoginTime  BIGINT      NOT NULL,
    playTime       BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}data"
(
    id       SERIAL       NOT NULL PRIMARY KEY,
    uuid     VARCHAR(36)  NOT NULL,
    type     VARCHAR(16)  NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    value    TEXT         NOT NULL,
    expiry   BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}servers"
(
    id             SERIAL      NOT NULL PRIMARY KEY,
    name           VARCHAR(16) NOT NULL,
    type           VARCHAR(16) NOT NULL,
    autoSync1      BOOLEAN     NOT NULL,
    autoSync2      BOOLEAN     NOT NULL,
    lastActiveTime BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}chat"
(
    id        SERIAL      NOT NULL PRIMARY KEY,
    name      VARCHAR(16) NOT NULL,
    records   TEXT        NOT NULL,
    startTime BIGINT      NOT NULL,
    endTime   BIGINT      NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}report"
(
    id             SERIAL      NOT NULL PRIMARY KEY,
    uuid           VARCHAR(36) NOT NULL,
    reporter       VARCHAR(36) NOT NULL,
    reported       VARCHAR(36) NOT NULL,
    reason         VARCHAR(20) NOT NULL,
    reportTime     BIGINT      NOT NULL,
    handler        VARCHAR(36) NULL,
    handleTime     BIGINT      NULL,
    conclusion     BOOLEAN     NULL,
    conclusionTime BIGINT      NULL,
    chat           TEXT        NOT NULL
);
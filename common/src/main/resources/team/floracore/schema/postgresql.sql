-- FloraCore PostgreSQL Schema

CREATE TABLE IF NOT EXISTS "{prefix}player"
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
    type     VARCHAR(64)  NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    value    TEXT         NOT NULL,
    expiry   BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}data_int"
(
    id       SERIAL       NOT NULL PRIMARY KEY,
    uuid     VARCHAR(36)  NOT NULL,
    type     VARCHAR(64)  NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    value    INT          NOT NULL,
    expiry   BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}server"
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
    id         SERIAL      NOT NULL PRIMARY KEY,
    type       VARCHAR(16) NOT NULL,
    parameters VARCHAR(36) NOT NULL,
    uuid       VARCHAR(36) NOT NULL,
    message    TEXT        NOT NULL,
    time       BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}party"
(
    id          SERIAL      NOT NULL PRIMARY KEY,
    uuid        VARCHAR(36) NOT NULL,
    leader      VARCHAR(36) NOT NULL,
    moderators  TEXT        NOT NULL,
    members     TEXT        NOT NULL,
    settings    TEXT        NOT NULL,
    createTime  BIGINT      NOT NULL,
    disbandTime BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS "{prefix}online"
(
    uuid       VARCHAR(36) NOT NULL,
    status     BOOLEAN     NOT NULL,
    serverName VARCHAR(16) NOT NULL
);
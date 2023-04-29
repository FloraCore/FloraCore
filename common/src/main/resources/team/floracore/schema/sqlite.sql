-- FloraCore SQLite Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id             INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    uuid           VARCHAR(36) NOT NULL,
    name           VARCHAR(16) NOT NULL,
    firstLoginIp   VARCHAR(39) NOT NULL,
    lastLoginIp    VARCHAR(39) NOT NULL,
    firstLoginTime BIGINT      NOT NULL,
    lastLoginTime  BIGINT      NOT NULL,
    playTime       BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}data`
(
    id       INTEGER      NOT NULL PRIMARY KEY AUTOINCREMENT,
    uuid     VARCHAR(36)  NOT NULL,
    type     VARCHAR(16)  NOT NULL,
    data_key VARCHAR(255) NOT NULL,
    value    TEXT         NOT NULL,
    expiry   BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}servers`
(
    id             INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    name           VARCHAR(16) NOT NULL,
    type           VARCHAR(16) NOT NULL,
    autoSync1      BOOLEAN     NOT NULL,
    autoSync2      BOOLEAN     NOT NULL,
    lastActiveTime BIGINT      NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}chat`
(
    id        INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    name      VARCHAR(16) NOT NULL,
    records   TEXT        NOT NULL,
    startTime BIGINT      NOT NULL,
    endTime   BIGINT      NULL
);
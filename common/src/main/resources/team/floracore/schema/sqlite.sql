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
    value    VARCHAR(255) NOT NULL,
    expiry   BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}servers`
(
    id             INTEGER     NOT NULL PRIMARY KEY AUTOINCREMENT,
    name           VARCHAR(16) NOT NULL,
    type           VARCHAR(16) NOT NULL,
    autoSync       BOOLEAN     NOT NULL,
    lastActiveTime BIGINT      NOT NULL
);
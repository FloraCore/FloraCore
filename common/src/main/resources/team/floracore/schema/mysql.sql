-- FloraCore MySQL Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id             INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid           VARCHAR(36)        NOT NULL,
    name           VARCHAR(16)        NOT NULL,
    firstLoginIp   VARCHAR(39)        NOT NULL,
    lastLoginIp    VARCHAR(39)        NOT NULL,
    firstLoginTime BIGINT             NOT NULL,
    lastLoginTime  BIGINT             NOT NULL,
    playTime       BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}data`
(
    id       INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid     VARCHAR(36)        NOT NULL,
    type     VARCHAR(16)        NOT NULL,
    data_key VARCHAR(255)       NOT NULL,
    value    TEXT               NOT NULL,
    expiry   BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}servers`
(
    id             INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name           VARCHAR(16)        NOT NULL,
    type           VARCHAR(16)        NOT NULL,
    autoSync1      BOOLEAN            NOT NULL,
    autoSync2      BOOLEAN            NOT NULL,
    lastActiveTime BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}chat`
(
    id        INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    name      VARCHAR(16)        NOT NULL,
    record    TEXT               NOT NULL,
    startTime BIGINT             NOT NULL,
    endTime   BIGINT             NULL
);
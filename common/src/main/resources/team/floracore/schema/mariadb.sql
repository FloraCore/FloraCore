-- FloraCore MariaDB Schema

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
    records   TEXT               NOT NULL,
    startTime BIGINT             NOT NULL,
    endTime   BIGINT             NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}report`
(
    id             INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    reporter       VARCHAR(36)        NOT NULL,
    reported       VARCHAR(36)        NOT NULL,
    reason         VARCHAR(20)        NOT NULL,
    reportTime     BIGINT             NOT NULL,
    handler        VARCHAR(36)        NOT NULL,
    handleTime     BIGINT             NOT NULL,
    conclusion     VARCHAR(10)        NOT NULL,
    conclusionTime BIGINT             NOT NULL,
    chat           TEXT               NOT NULL
);
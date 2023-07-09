-- FloraCore MySQL Schema

CREATE TABLE IF NOT EXISTS `{prefix}player`
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
    type     VARCHAR(64)        NOT NULL,
    data_key VARCHAR(255)       NOT NULL,
    value    TEXT               NOT NULL,
    expiry   BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}data_int`
(
    id       INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid     VARCHAR(36)        NOT NULL,
    type     VARCHAR(64)        NOT NULL,
    data_key VARCHAR(255)       NOT NULL,
    value    INT                NOT NULL,
    expiry   BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}data_long`
(
    id       INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid     VARCHAR(36)        NOT NULL,
    type     VARCHAR(64)        NOT NULL,
    data_key VARCHAR(255)       NOT NULL,
    value    BIGINT             NOT NULL,
    expiry   BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}server`
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
    id         INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    type       VARCHAR(16)        NOT NULL,
    parameters VARCHAR(36)        NOT NULL,
    uuid       VARCHAR(36)        NOT NULL,
    message    TEXT               NOT NULL,
    time       BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}report`
(
    id             INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid           VARCHAR(36)        NOT NULL,
    reporters      TEXT               NOT NULL,
    reported       VARCHAR(36)        NOT NULL,
    reasons        TEXT               NOT NULL,
    reportTime     BIGINT             NOT NULL,
    status         VARCHAR(36)        NOT NULL,
    conclusionTime BIGINT             NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}party`
(
    id          INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    uuid        VARCHAR(36)        NOT NULL,
    leader      VARCHAR(36)        NOT NULL,
    moderators  TEXT               NOT NULL,
    members     TEXT               NOT NULL,
    settings    TEXT               NOT NULL,
    createTime  BIGINT             NOT NULL,
    disbandTime BIGINT             NOT NULL
);

CREATE TABLE IF NOT EXISTS `{prefix}online`
(
    uuid       VARCHAR(36) NOT NULL,
    status     BOOLEAN     NOT NULL,
    serverName VARCHAR(16) NOT NULL
);
-- FloraCore MariaDB Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id             INT AUTO_INCREMENT                 NOT NULL PRIMARY KEY,
    uuid           VARCHAR(32)                        NOT NULL,
    name           VARCHAR(36)                        NOT NULL,
    firstLoginIp   VARCHAR(39)                        NOT NULL,
    lastLoginIp    VARCHAR(39)                        NOT NULL,
    firstLoginTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastLoginTime  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    playTime       BIGINT                             NOT NULL
);


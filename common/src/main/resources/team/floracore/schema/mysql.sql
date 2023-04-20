-- FloraCore MySQL Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id         INT AUTO_INCREMENT                 NOT NULL PRIMARY KEY,
    uuid       VARCHAR(32)                        NOT NULL,
    name       VARCHAR(36)                        NOT NULL,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);


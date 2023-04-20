-- FloraCore MariaDB Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id         BIGINT AUTO_INCREMENT              NOT NULL PRIMARY KEY,
    uuid       VARCHAR(32)                        NOT NULL,
    name       VARCHAR(36)                        NOT NULL,
    createTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
);


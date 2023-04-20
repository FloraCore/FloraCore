-- FloraCore SQLite Schema

CREATE TABLE IF NOT EXISTS `{prefix}players`
(
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid       VARCHAR(32)                        NOT NULL,
    name       VARCHAR(36)                        NOT NULL,
    firstLogin DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    lastLogin  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL
);
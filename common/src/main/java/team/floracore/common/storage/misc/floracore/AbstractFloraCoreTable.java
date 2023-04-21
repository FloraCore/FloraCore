package team.floracore.common.storage.misc.floracore;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.sql.*;

public abstract class AbstractFloraCoreTable implements FloraCoreTable {
    private final FloraCorePlugin plugin;
    private final SqlStorage sqlStorage;

    public AbstractFloraCoreTable(FloraCorePlugin plugin, SqlStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public SqlStorage getSqlStorage() {
        return sqlStorage;
    }
}

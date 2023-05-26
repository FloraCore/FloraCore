package team.floracore.common.storage;

import com.google.common.collect.*;
import team.floracore.common.config.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.implementation.sql.*;
import team.floracore.common.storage.implementation.sql.connection.file.*;
import team.floracore.common.storage.implementation.sql.connection.hikari.*;

import java.util.*;

/**
 * 数据库操作的主工厂类。
 */
public class StorageFactory {
    private final FloraCorePlugin plugin;

    public StorageFactory(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Set<StorageType> getRequiredTypes() {
        return ImmutableSet.of(this.plugin.getConfiguration().get(ConfigKeys.STORAGE_METHOD));
    }

    public Storage getInstance() {
        Storage storage;
        StorageType type = this.plugin.getConfiguration().get(ConfigKeys.STORAGE_METHOD);
        this.plugin.getLogger().info("Loading storage provider... [" + type.name() + "]");
        storage = new Storage(this.plugin, createNewImplementation(type));

        storage.init();
        return storage;
    }

    private StorageImplementation createNewImplementation(StorageType method) {
        switch (method) {
            case MARIADB:
                return new SqlStorage(
                        this.plugin,
                        new MariaDbConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case MYSQL:
                return new SqlStorage(
                        this.plugin,
                        new MySqlConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case SQLITE:
                return new SqlStorage(
                        this.plugin,
                        new SqliteConnectionFactory(this.plugin.getBootstrap()
                                .getDataDirectory()
                                .resolve("floracore-sqlite.db")),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case H2:
                return new SqlStorage(
                        this.plugin,
                        new H2ConnectionFactory(this.plugin.getBootstrap()
                                .getDataDirectory()
                                .resolve("floracore-h2-v2")),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            case POSTGRESQL:
                return new SqlStorage(
                        this.plugin,
                        new PostgresConnectionFactory(this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                        this.plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX)
                );
            default:
                throw new RuntimeException("Unknown method: " + method);
        }
    }
}

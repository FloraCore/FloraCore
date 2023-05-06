package team.floracore.common.config;

import com.google.common.collect.*;
import org.floracore.api.server.*;
import team.floracore.common.config.generic.*;
import team.floracore.common.config.generic.key.*;
import team.floracore.common.storage.*;
import team.floracore.common.storage.misc.*;

import java.util.*;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class ConfigKeys {
    /**
     * If FloraCore should automatically install translation bundles and periodically update them.
     */
    public static final ConfigKey<Boolean> AUTO_INSTALL_TRANSLATIONS = notReloadable(booleanKey("auto-install-translations", true));

    /**
     * The database settings, username, password, etc. for use by any database
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES = notReloadable(key(c -> {
        int maxPoolSize = c.getInteger("data.pool-settings.maximum-pool-size", c.getInteger("data.pool-size", 10));
        int minIdle = c.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = c.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int keepAliveTime = c.getInteger("data.pool-settings.keepalive-time", 0);
        int connectionTimeout = c.getInteger("data.pool-settings.connection-timeout", 5000);
        Map<String, String> props = ImmutableMap.copyOf(c.getStringMap("data.pool-settings.properties", ImmutableMap.of()));

        return new StorageCredentials(c.getString("data.address", null), c.getString("data.database", null), c.getString("data.username", null), c.getString("data.password", null), maxPoolSize, minIdle, maxLifetime, keepAliveTime, connectionTimeout, props);
    }));
    /**
     * The prefix for any SQL tables
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX = notReloadable(key(c -> {
        return c.getString("data.table-prefix", c.getString("data.table_prefix", "floracore_"));
    }));
    /**
     * The name of the storage method being used
     */
    public static final ConfigKey<StorageType> STORAGE_METHOD = notReloadable(key(c -> StorageType.parse(c.getString("storage-method", "h2"), StorageType.H2)));


    /**
     * If redis messaging is enabled
     */
    public static final ConfigKey<Boolean> REDIS_ENABLED = notReloadable(booleanKey("redis.enabled", false));

    /**
     * The address of the redis server
     */
    public static final ConfigKey<String> REDIS_ADDRESS = notReloadable(stringKey("redis.address", null));

    /**
     * The username to connect with, or an empty string if it should use default
     */
    public static final ConfigKey<String> REDIS_USERNAME = notReloadable(stringKey("redis.username", ""));

    /**
     * The password in use by the redis server, or an empty string if there is no password
     */
    public static final ConfigKey<String> REDIS_PASSWORD = notReloadable(stringKey("redis.password", ""));

    /**
     * If the redis connection should use SSL
     */
    public static final ConfigKey<Boolean> REDIS_SSL = notReloadable(booleanKey("redis.ssl", false));

    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK_PERMISSION = notReloadable(mapKey("commands.nick.rank-permission"));

    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK = notReloadable(mapKey("commands.nick.rank"));
    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK_PREFIX = notReloadable(mapKey("commands.nick.rank-prefix"));
    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_SIGN = notReloadable(mapKey("commands.nick.sign"));

    public static final ConfigKey<Double> SPEED_MAX_FLY_SPEED = notReloadable(key(c -> {
        final double maxSpeed = c.getDouble("commands.speed.max-fly-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    }));

    public static final ConfigKey<Double> SPEED_MAX_WALK_SPEED = notReloadable(key(c -> {
        final double maxSpeed = c.getDouble("commands.speed.max-walk-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    }));

    public static final ConfigKey<String> SERVER_NAME = notReloadable(stringKey("server.name", "unknown"));
    public static final ConfigKey<ServerType> SERVER_TYPE = notReloadable(key(c -> ServerType.parse(c.getString("server.type", "unknown"), ServerType.UNKNOWN)));

    public static final ConfigKey<Boolean> CHECK_UPDATE = notReloadable(booleanKey("check-update", false));

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    private ConfigKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

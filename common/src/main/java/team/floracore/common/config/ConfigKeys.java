package team.floracore.common.config;

import com.google.common.collect.ImmutableMap;
import org.floracore.api.server.ServerType;
import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;
import team.floracore.common.storage.StorageType;
import team.floracore.common.storage.misc.StorageCredentials;

import java.util.List;
import java.util.Map;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class ConfigKeys {
    public static final ConfigKey<Boolean> BUNGEECORD = booleanKey("bungeecord", false);

    /**
     * If FloraCore should automatically install translation bundles and periodically update them.
     */
    public static final ConfigKey<Boolean> AUTO_INSTALL_TRANSLATIONS = notReloadable(booleanKey(
            "auto-install-translations",
            true));

    public static final ConfigKey<Boolean> COMMANDS_NICK_ENABLE = notReloadable(booleanKey(
            "commands.nick.enable",
            true));

    /**
     * The database settings, username, password, etc. for use by any database
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES = notReloadable(key(c -> {
        int maxPoolSize = c.getInteger("data.pool-settings.maximum-pool-size", c.getInteger("data.pool-size", 10));
        int minIdle = c.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
        int maxLifetime = c.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int keepAliveTime = c.getInteger("data.pool-settings.keepalive-time", 0);
        int connectionTimeout = c.getInteger("data.pool-settings.connection-timeout", 5000);
        Map<String, String> props = ImmutableMap.copyOf(c.getStringMap("data.pool-settings.properties",
                ImmutableMap.of()));

        return new StorageCredentials(c.getString("data.address", null),
                c.getString("data.database", null),
                c.getString("data.username", null),
                c.getString("data.password", null),
                maxPoolSize,
                minIdle,
                maxLifetime,
                keepAliveTime,
                connectionTimeout,
                props);
    }));

    /**
     * The prefix for any SQL tables
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX = notReloadable(key(c -> c.getString("data.table-prefix", c.getString("data.table_prefix", "floracore_"))));

    /**
     * The name of the storage method being used
     */
    public static final ConfigKey<StorageType> STORAGE_METHOD = notReloadable(key(c -> StorageType.parse(c.getString(
            "storage-method",
            "h2"), StorageType.H2)));

    /**
     * The name of the messaging service in use, or "none" if not enabled
     */
    public static final ConfigKey<String> MESSAGING_SERVICE = notReloadable(lowercaseStringKey("messaging-service",
            "auto"));

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

    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK_PERMISSION = mapKey(
            "commands.nick.rank-permission");

    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK = mapKey("commands.nick.rank");
    public static final ConfigKey<Map<String, String>> COMMANDS_NICK_RANK_PREFIX = mapKey(
            "commands.nick.rank-prefix");

    public static final ConfigKey<Double> SPEED_MAX_FLY_SPEED = key(c -> {
        final double maxSpeed = c.getDouble("commands.speed.max-fly-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    });

    public static final ConfigKey<Double> SPEED_MAX_WALK_SPEED = key(c -> {
        final double maxSpeed = c.getDouble("commands.speed.max-walk-speed", 0.8);
        return maxSpeed > 1.0 ? 1.0 : Math.abs(maxSpeed);
    });

    public static final ConfigKey<String> SERVER_NAME = notReloadable(stringKey("server.name", "unknown"));
    public static final ConfigKey<ServerType> SERVER_TYPE = notReloadable(key(c -> ServerType.parse(c.getString(
            "server.type",
            "unknown"), ServerType.UNKNOWN)));

    public static final ConfigKey<Boolean> CHECK_UPDATE = notReloadable(booleanKey("check-update", true));

    /**
     * The URL of the bytebin instance used to upload data
     */
    public static final ConfigKey<String> BYTEBIN_URL = stringKey("bytebin-url", "https://bytebin.floracore.cc/");

    /**
     * The host of the bytesocks instance used to communicate with
     */
    public static final ConfigKey<String> BYTESOCKS_HOST = stringKey("bytesocks-host", "bytesocks.floracore.cc");

    /**
     * The URL of the verbose viewer
     */
    public static final ConfigKey<String> CHAT_VIEWER_URL_PATTERN = stringKey("chat-viewer-url", "https://floracore" +
            ".cc/chat/");


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

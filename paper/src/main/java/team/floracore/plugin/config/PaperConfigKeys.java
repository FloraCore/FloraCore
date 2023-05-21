package team.floracore.plugin.config;

import org.floracore.api.server.*;
import team.floracore.common.config.generic.*;
import team.floracore.common.config.generic.key.*;

import java.util.*;

import static team.floracore.common.config.generic.key.ConfigKeyFactory.*;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class PaperConfigKeys {
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

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(PaperConfigKeys.class);

    private PaperConfigKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

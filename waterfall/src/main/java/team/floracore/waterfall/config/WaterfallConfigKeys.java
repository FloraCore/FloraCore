package team.floracore.waterfall.config;

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
public class WaterfallConfigKeys {
    public static final ConfigKey<String> SERVER_NAME = notReloadable(stringKey("server-name", "unknown"));

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(WaterfallConfigKeys.class);

    private WaterfallConfigKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

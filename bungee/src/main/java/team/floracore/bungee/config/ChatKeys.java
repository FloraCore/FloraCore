package team.floracore.bungee.config;

import team.floracore.common.config.generic.KeyedConfiguration;
import team.floracore.common.config.generic.key.ConfigKey;
import team.floracore.common.config.generic.key.SimpleConfigKey;

import java.util.List;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class ChatKeys {

    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(ChatKeys.class);

    private ChatKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

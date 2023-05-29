package team.floracore.bukkit.config;

import team.floracore.common.config.generic.*;
import team.floracore.common.config.generic.key.*;

import java.util.*;

/**
 * All of the {@link ConfigKey}s used by FloraCore.
 *
 * <p>The {@link #getKeys()} method and associated behaviour allows this class
 * to function a bit like an enum, but with generics.</p>
 */
public class BoardsKeys {
    /**
     * A list of the keys defined in this class.
     */
    private static final List<SimpleConfigKey<?>> KEYS = KeyedConfiguration.initialise(BoardsKeys.class);

    private BoardsKeys() {
    }

    public static List<? extends ConfigKey<?>> getKeys() {
        return KEYS;
    }
}

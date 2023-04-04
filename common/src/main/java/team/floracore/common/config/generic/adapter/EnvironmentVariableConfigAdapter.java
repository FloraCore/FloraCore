package team.floracore.common.config.generic.adapter;

import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.*;

import java.util.*;

public class EnvironmentVariableConfigAdapter extends StringBasedConfigurationAdapter {
    private static final String PREFIX = "FLORACORE_";

    private final FloraCorePlugin plugin;

    public EnvironmentVariableConfigAdapter(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected @Nullable String resolveValue(String path) {
        // e.g.
        // 'server'            -> FLORACORE_SERVER
        // 'data.table_prefix' -> FLORACORE_DATA_TABLE_PREFIX
        String key = PREFIX + path.toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace('.', '_');

        String value = System.getenv(key);
        if (value != null) {
            this.plugin.getLogger().info("Resolved configuration value from environment variable: " + key + " = " + (path.contains("password") ? "*****" : value));
        }
        return value;
    }

    @Override
    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public void reload() {
        // no-op
    }
}

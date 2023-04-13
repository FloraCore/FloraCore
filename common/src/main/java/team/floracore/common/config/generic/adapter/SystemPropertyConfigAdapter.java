package team.floracore.common.config.generic.adapter;

import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.*;

public class SystemPropertyConfigAdapter extends StringBasedConfigurationAdapter {
    private static final String PREFIX = "floracore.";

    private final FloraCorePlugin plugin;

    public SystemPropertyConfigAdapter(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected @Nullable String resolveValue(String path) {
        // e.g.
        // 'server'            -> floracore.server
        // 'data.table_prefix' -> floracore.data.table-prefix
        String key = PREFIX + path;

        String value = System.getProperty(key);
        if (value != null) {
            this.plugin.getLogger().info("Resolved configuration value from system property: " + key + " = " + (path.contains("password") ? "*****" : value));
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

package team.floracore.common.config;

import team.floracore.common.config.generic.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.plugin.*;

public class FloraCoreConfiguration extends KeyedConfiguration {
    private final FloraCorePlugin plugin;

    public FloraCoreConfiguration(FloraCorePlugin plugin, ConfigurationAdapter adapter) {
        super(adapter, ConfigKeys.getKeys());
        this.plugin = plugin;

        init();
    }

    @Override
    protected void load(boolean initial) {
        super.load(initial);
    }

    @Override
    public void reload() {
        super.reload();
    }

    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }
}

package team.floracore.bukkit.config;

import team.floracore.bukkit.*;
import team.floracore.common.config.generic.*;
import team.floracore.common.config.generic.adapter.*;
import team.floracore.common.plugin.*;

public class BoardsConfiguration extends KeyedConfiguration {
    private final FCBukkitPlugin plugin;

    public BoardsConfiguration(FCBukkitPlugin plugin, ConfigurationAdapter adapter) {
        super(adapter, BoardsKeys.getKeys());
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

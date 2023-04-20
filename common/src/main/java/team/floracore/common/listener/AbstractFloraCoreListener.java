package team.floracore.common.listener;

import team.floracore.common.plugin.*;

public abstract class AbstractFloraCoreListener implements FloraCoreListener {
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreListener(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

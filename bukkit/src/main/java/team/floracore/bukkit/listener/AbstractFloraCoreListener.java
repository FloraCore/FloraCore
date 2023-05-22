package team.floracore.bukkit.listener;

import org.bukkit.event.*;
import team.floracore.common.plugin.*;

public abstract class AbstractFloraCoreListener implements FloraCoreListener, Listener {
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreListener(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

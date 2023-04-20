package team.floracore.common.listener;

import team.floracore.common.plugin.*;

public class ListenerManager {
    private final FloraCorePlugin plugin;

    public ListenerManager(FloraCorePlugin plugin) {
        this.plugin = plugin;

        // Create the commands
        this.constructListeners();
    }

    private void constructListeners() {

    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

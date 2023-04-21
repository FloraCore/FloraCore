package team.floracore.common.listener;

import org.bukkit.plugin.*;
import team.floracore.common.listeners.*;
import team.floracore.common.plugin.*;

public class ListenerManager {
    private final FloraCorePlugin plugin;

    public ListenerManager(FloraCorePlugin plugin) {
        this.plugin = plugin;

        // Create the commands
        this.constructListeners();
    }

    private void constructListeners() {
        PluginManager pm = plugin.getBootstrap().getServer().getPluginManager();
        Plugin p = plugin.getBootstrap().getPlugin();
        pm.registerEvents(new PlayerLoginListener(plugin), p);
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

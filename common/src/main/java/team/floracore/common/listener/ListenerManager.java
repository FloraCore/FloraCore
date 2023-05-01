package team.floracore.common.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import team.floracore.common.listeners.*;
import team.floracore.common.plugin.*;

public class ListenerManager {
    private final FloraCorePlugin plugin;

    public ListenerManager(FloraCorePlugin plugin) {
        this.plugin = plugin;

        // Create the Listeners
        this.constructListeners();
    }

    public PluginManager getPluginManager() {
        return plugin.getBootstrap().getServer().getPluginManager();
    }

    private void constructListeners() {
        PluginManager pm = plugin.getBootstrap().getServer().getPluginManager();
        Plugin p = plugin.getBootstrap().getPlugin();
        pm.registerEvents(new PlayerLoginListener(plugin), p);
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        PluginManager pm = plugin.getBootstrap().getServer().getPluginManager();
        Plugin p = plugin.getBootstrap().getPlugin();
        pm.registerEvents(listener, p);
    }
}

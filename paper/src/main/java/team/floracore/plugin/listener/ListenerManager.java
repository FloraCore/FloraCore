package team.floracore.plugin.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import team.floracore.common.plugin.*;
import team.floracore.plugin.*;
import team.floracore.plugin.listeners.*;

public class ListenerManager {
    private final FCBukkitPlugin plugin;

    public ListenerManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        // Create the Listeners
        this.constructListeners();
    }

    public PluginManager getPluginManager() {
        return plugin.getBootstrap().getServer().getPluginManager();
    }

    private void constructListeners() {
        PluginManager pm = plugin.getBootstrap().getServer().getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerEvents(new PlayerListener(plugin), p);
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        PluginManager pm = plugin.getBootstrap().getServer().getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerEvents(listener, p);
    }
}

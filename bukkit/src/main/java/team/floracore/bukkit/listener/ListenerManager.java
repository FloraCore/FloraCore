package team.floracore.bukkit.listener;

import org.bukkit.event.*;
import org.bukkit.plugin.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.listeners.*;
import team.floracore.common.plugin.*;

public class ListenerManager {
    private final FCBukkitPlugin plugin;

    public ListenerManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        // Create the Listeners
        this.constructListeners();
    }

    private void constructListeners() {
        PluginManager pm = getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerEvents(new PlayerListener(plugin), p);
        pm.registerEvents(new ScoreboardListener(plugin), p);
    }

    public PluginManager getPluginManager() {
        return plugin.getBootstrap().getServer().getPluginManager();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        PluginManager pm = getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerEvents(listener, p);
    }
}

package team.floracore.bungee.listener;

import net.md_5.bungee.api.plugin.*;
import team.floracore.bungee.*;
import team.floracore.bungee.listeners.*;
import team.floracore.common.plugin.*;

public class ListenerManager {
    private final FCBungeePlugin plugin;

    public ListenerManager(FCBungeePlugin plugin) {
        this.plugin = plugin;
        // Create the Listeners
        this.constructListeners();
    }

    private void constructListeners() {
        PluginManager pm = getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerListener(p, new PlayerListener(plugin));
    }

    public PluginManager getPluginManager() {
        return plugin.getBootstrap().getProxy().getPluginManager();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        PluginManager pm = getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerListener(p, listener);
    }
}

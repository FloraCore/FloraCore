package team.floracore.bungee.listener;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.listener.impl.PlayerListener;
import team.floracore.common.plugin.FloraCorePlugin;

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
        return plugin.getProxy().getPluginManager();
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

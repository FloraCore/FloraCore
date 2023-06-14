package team.floracore.bukkit.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.config.BoardsKeys;
import team.floracore.bukkit.listeners.PlayerListener;
import team.floracore.bukkit.listeners.ScoreboardListener;

public class ListenerManager {
    private final FCBukkitPlugin plugin;
    private final Plugin loader;
    private final PluginManager pluginManager = Bukkit.getPluginManager();

    public ListenerManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
        this.loader = plugin.getLoader();
        // Create the Listeners
        this.constructListeners();
    }

    private void constructListeners() {
        registerListener(new PlayerListener(plugin));
        if (getPlugin().getBoardsConfiguration().get(BoardsKeys.ENABLE)) {
            registerListener(new ScoreboardListener(plugin));
        }
    }

    public FCBukkitPlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, loader);
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}

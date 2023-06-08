package team.floracore.bukkit.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.config.BoardsKeys;
import team.floracore.bukkit.listeners.PlayerListener;
import team.floracore.bukkit.listeners.ScoreboardListener;

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
        if (getPlugin().getBoardsConfiguration().get(BoardsKeys.ENABLE)) {
            pm.registerEvents(new ScoreboardListener(plugin), p);
        }
    }

    public PluginManager getPluginManager() {
        return plugin.getBootstrap().getServer().getPluginManager();
    }

    public FCBukkitPlugin getPlugin() {
        return plugin;
    }

    public void registerListener(Listener listener) {
        PluginManager pm = getPluginManager();
        Plugin p = plugin.getBootstrap().getLoader();
        pm.registerEvents(listener, p);
    }
}

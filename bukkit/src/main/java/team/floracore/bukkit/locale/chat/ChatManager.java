package team.floracore.bukkit.locale.chat;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import team.floracore.bukkit.*;
import team.floracore.common.plugin.*;

import java.util.*;

public class ChatManager implements Listener {
    private final FCBukkitPlugin plugin;

    public ChatManager(FCBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void shutdown() {
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        long time = System.currentTimeMillis();
        String message = e.getMessage();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        long time = System.currentTimeMillis();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
    }
}

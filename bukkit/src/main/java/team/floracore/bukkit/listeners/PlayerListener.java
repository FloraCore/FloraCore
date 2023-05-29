package team.floracore.bukkit.listeners;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.config.*;
import team.floracore.bukkit.listener.*;
import team.floracore.bukkit.scoreboard.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.sql.*;
import java.util.*;

public class PlayerListener extends FloraCoreBukkitListener {
    private final FCBukkitPlugin plugin;
    private final ScoreBoardManager manager;

    public PlayerListener(FCBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
        manager = plugin.getScoreBoardManager();
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID u = e.getUniqueId();
        String name = e.getName();
        String ip = e.getAddress().getHostAddress();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        // 初始化玩家数据
        PLAYER p = storageImplementation.selectPlayer(u);
        if (p == null) {
            p = new PLAYER(getPlugin(), storageImplementation, -1, u, name, ip);
            try {
                p.init();
            } catch (SQLException ex) {
                throw new RuntimeException("玩家初始化失败！");
            }
        } else {
            p.setName(name);
            p.setLastLoginIp(ip);
            long currentTime = System.currentTimeMillis();
            p.setLastLoginTime(currentTime);
        }
        // 清除过期数据
        storageImplementation.deleteDataExpired(u);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        getPlugin().getBootstrap().getScheduler().async().execute(() -> {
            ONLINE online = storageImplementation.selectOnline(uuid);
            if (online == null) {
                storageImplementation.insertOnline(uuid, true, getPlugin().getServerName());
            } else {
                online.setServerName(getPlugin().getServerName());
                online.setStatusTrue();
            }
            storageImplementation.insertData(uuid, DataType.FUNCTION, "server-status", getPlugin().getServerName(), 0);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        getPlugin().getBootstrap().getScheduler().async().execute(() -> {
            ONLINE online = storageImplementation.selectOnline(uuid);
            if (online == null) {
                storageImplementation.insertOnline(uuid, false, getPlugin().getServerName());
            } else {
                online.setStatusFalse(getPlugin().getServerName());
            }
        });
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (plugin.getBoardsConfiguration().get(BoardsKeys.DISABLE_WORLDS).contains(player.getWorld().getName())) {
            manager.removeTarget(e.getPlayer());
        } else {
            manager.addTarget(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        manager.addTarget(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        manager.removeTarget(e.getPlayer());
    }
}

package team.floracore.bungee.listeners;

import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.event.*;
import team.floracore.bungee.listener.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.sql.*;
import java.util.*;

public class PlayerListener extends FloraCoreBungeeListener {
    public PlayerListener(FloraCorePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(PostLoginEvent e) {
        ProxiedPlayer player = e.getPlayer();
        UUID u = player.getUniqueId();
        String name = player.getName();
        String ip = player.getSocketAddress().toString();
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
        storageImplementation.deleteDataExpired(u);
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
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
        });
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent e) {
        ProxiedPlayer p = e.getPlayer();
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
}

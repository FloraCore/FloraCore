package team.floracore.bungee.listener.impl;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import team.floracore.bungee.listener.FloraCoreBungeeListener;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.tables.ONLINE;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;

import java.sql.SQLException;
import java.util.UUID;

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
        storageImplementation.deleteDataIntExpired(u);
        storageImplementation.deleteDataLongExpired(u);
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        getAsyncExecutor().execute(() -> {
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
        getAsyncExecutor().execute(() -> {
            ONLINE online = storageImplementation.selectOnline(uuid);
            if (online == null) {
                storageImplementation.insertOnline(uuid, false, getPlugin().getServerName());
            } else {
                online.setStatusFalse(getPlugin().getServerName());
            }
        });
    }
}

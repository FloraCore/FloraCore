package team.floracore.common.listeners;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import team.floracore.common.listener.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class PlayerLoginListener extends AbstractFloraCoreListener {
    public PlayerLoginListener(FloraCorePlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID u = e.getUniqueId();
        String name = e.getName();
        String ip = e.getAddress().getHostAddress();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        // 初始化玩家数据
        Players p = storageImplementation.selectPlayers(u, name, ip);
        p.setName(name);
        p.setLastLoginIp(ip);
        long currentTime = System.currentTimeMillis();
        p.setLastLoginTime(currentTime);
        // 清除过期数据
        storageImplementation.deleteDataExpired(u);
    }
}
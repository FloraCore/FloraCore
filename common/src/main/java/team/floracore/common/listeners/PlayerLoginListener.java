package team.floracore.common.listeners;

import org.bukkit.event.*;
import org.bukkit.event.player.*;
import team.floracore.common.listener.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;

import java.util.*;

public class PlayerLoginListener extends AbstractFloraCoreListener {
    public PlayerLoginListener(FloraCorePlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        UUID u = e.getUniqueId();
        String name = e.getName();
        String ip = e.getAddress().getHostAddress();
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        storageImplementation.selectPlayerBaseInfo(u, name, ip);
    }
}

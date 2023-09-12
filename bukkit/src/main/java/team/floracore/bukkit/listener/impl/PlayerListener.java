package team.floracore.bukkit.listener.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.floracore.api.data.DataType;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.listener.FloraCoreBukkitListener;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.tables.ONLINE;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerListener extends FloraCoreBukkitListener {
	private final FCBukkitPlugin plugin;

	public PlayerListener(FCBukkitPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		UUID u = e.getUniqueId();
		String name = e.getName();
		String ip = e.getAddress().getHostAddress();
		StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
		getAsyncExecutor().execute(() -> {
			// 初始化玩家数据
			PLAYER p = storageImplementation.selectPlayer(u);
			if (p == null) {
				p = new PLAYER(getPlugin(), storageImplementation, -1, u, name, ip);
				try {
					p.init();
				} catch (SQLException ex) {
					throw new RuntimeException("Player initialization failed!");
				}
			} else {
				p.setName(name);
				p.setLastLoginIp(ip);
				long currentTime = System.currentTimeMillis();
				p.setLastLoginTime(currentTime);
			}
			// 清除过期数据
			storageImplementation.deleteDataExpired(u);
			storageImplementation.deleteDataIntExpired(u);
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
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
			storageImplementation.insertData(uuid, DataType.FUNCTION, "server-status", getPlugin().getServerName(), 0);
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
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

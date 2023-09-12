package team.floracore.bungee.listener.impl;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import team.floracore.bungee.listener.FloraCoreBungeeListener;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.tables.ONLINE;

import java.util.UUID;

public class PlayerListener extends FloraCoreBungeeListener {
	public PlayerListener(FloraCorePlugin plugin) {
		super(plugin);
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

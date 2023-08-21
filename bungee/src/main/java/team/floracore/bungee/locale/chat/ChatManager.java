package team.floracore.bungee.locale.chat;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;
import org.floracore.api.data.chat.ChatType;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.listener.FloraCoreBungeeListener;
import team.floracore.common.storage.misc.floracore.tables.SERVER;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author xLikeWATCHDOG
 */
public class ChatManager extends FloraCoreBungeeListener {
	public ChatManager(FCBungeePlugin plugin) {
		super(plugin);
		plugin.getListenerManager().registerListener(this);
	}

	public void shutdown() {
		// TODO shutdown
	}


	@EventHandler
	public void onChat(ChatEvent e) {
		if (e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) e.getSender();
			UUID uuid = player.getUniqueId();
			long time = System.currentTimeMillis();
			String message = e.getMessage();
			String name = player.getServer().getInfo().getName();
			getAsyncExecutor().execute(() -> {
				if (!hasServer(name)) {
					getStorageImplementation().insertChat(ChatType.SERVER,
							name,
							uuid,
							message,
							time);
				}
			});
		}
	}

	public boolean hasServer(String name) {
		for (String s : getStorageImplementation().selectServerList().stream().map(SERVER::getName).collect(Collectors.toSet())) {
			if (s.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}

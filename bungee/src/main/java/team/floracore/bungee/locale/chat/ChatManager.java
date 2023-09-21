package team.floracore.bungee.locale.chat;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;
import org.floracore.api.model.data.chat.ChatType;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.listener.FloraCoreBungeeListener;

import java.util.UUID;

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
				getStorageImplementation().insertChat(ChatType.SERVER,
						name,
						uuid,
						message,
						time);
			});
		}
	}
}

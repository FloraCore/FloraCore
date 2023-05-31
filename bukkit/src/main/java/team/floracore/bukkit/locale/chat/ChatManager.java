package team.floracore.bukkit.locale.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.floracore.api.data.chat.ChatType;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.listener.FloraCoreBukkitListener;

import java.util.UUID;

public class ChatManager extends FloraCoreBukkitListener {

    public ChatManager(FCBukkitPlugin plugin) {
        super(plugin);
        plugin.getListenerManager().registerListener(this);
    }

    public void shutdown() {
        // TODO shutdown
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        long time = System.currentTimeMillis();
        String message = e.getMessage();
        getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.SERVER,
                                                                               getPlugin().getServerName(),
                                                                               uuid,
                                                                               message,
                                                                               time));
    }
}

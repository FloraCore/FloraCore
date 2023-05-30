package team.floracore.bukkit.locale.chat;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.chat.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.listener.*;

import java.util.*;

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
        getAsyncExecutor().execute(() -> getImplementation().insertChat(ChatType.SERVER, getPlugin().getServerName(), uuid, message, time));
    }
}

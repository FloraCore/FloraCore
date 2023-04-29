package team.floracore.common.locale.chat;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class ChatManager implements Listener {
    private final FloraCorePlugin plugin;
    private final Chat chat;

    public ChatManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        long startTime = System.currentTimeMillis();
        plugin.getStorage().getImplementation().insertChat(plugin.getServerName(), startTime);
        this.chat = plugin.getStorage().getImplementation().selectChatWithStartTime(plugin.getServerName(), startTime);
        plugin.getListenerManager().registerListener(this);
        plugin.getBootstrap().getScheduler().async().execute(() -> {
            List<Chat> ret = plugin.getStorage().getImplementation().selectChat(plugin.getServerName());
            for (Chat i : ret) {
                if (i.getId() != this.chat.getId()) {
                    if (i.getEndTime() <= 0) {
                        i.setEndTime(startTime);
                    }
                }
            }
        });
    }

    public Chat getChat() {
        return chat;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void shutdown() {
        long endTime = System.currentTimeMillis();
        this.chat.setEndTime(endTime);
    }

    public void addChatRecord(ChatRecord chatRecord) {
        List<ChatRecord> chatRecords = this.chat.getRecords();
        chatRecords.add(chatRecord);
        this.chat.setRecords(chatRecords);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String message = e.getMessage();
        long time = System.currentTimeMillis();
        int id = this.chat.getRecords().size() + 1;
        ChatRecord chatRecord = new ChatRecord(id, uuid, message, time);
        this.plugin.getBootstrap().getScheduler().async().execute(() -> addChatRecord(chatRecord));
    }
}

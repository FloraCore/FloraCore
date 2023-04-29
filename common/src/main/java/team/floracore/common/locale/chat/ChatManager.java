package team.floracore.common.locale.chat;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

public class ChatManager {
    private final FloraCorePlugin plugin;
    private final Chat chat;

    public ChatManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        long startTime = System.currentTimeMillis();
        plugin.getStorage().getImplementation().insertChat(plugin.getServerName(), startTime);
        this.chat = plugin.getStorage().getImplementation().selectChatWithStartTime(plugin.getServerName(), startTime);
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
}

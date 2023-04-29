package team.floracore.common.locale.chat;

import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class ChatManager {
    private final FloraCorePlugin plugin;
    private final Chat chat;

    public ChatManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        long startTime = System.currentTimeMillis();
        plugin.getStorage().getImplementation().insertChat(plugin.getServerName(), startTime);
        this.chat = plugin.getStorage().getImplementation().selectChatWithStartTime(plugin.getServerName(), startTime);
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
}

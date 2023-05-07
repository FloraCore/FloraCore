package team.floracore.common.api.implementation;

import org.floracore.api.data.chat.*;
import team.floracore.common.locale.chat.*;
import team.floracore.common.plugin.*;

import java.util.*;

public class ApiChat implements ChatAPI {
    private final FloraCorePlugin plugin;
    private final ChatManager chatManager;

    public ApiChat(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    public UUID getPlayerChatUUID(UUID uuid) {
        return chatManager.getPlayerChatUUID(uuid);
    }
}

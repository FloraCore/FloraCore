package team.floracore.common.api.implementation;

import org.floracore.api.data.chat.ChatAPI;
import team.floracore.common.plugin.FloraCorePlugin;

public class ApiChat implements ChatAPI {
    private final FloraCorePlugin plugin;

    public ApiChat(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

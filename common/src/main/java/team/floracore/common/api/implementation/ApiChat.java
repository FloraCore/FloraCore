package team.floracore.common.api.implementation;

import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;

public class ApiChat implements ChatAPI {
    private final FloraCorePlugin plugin;

    public ApiChat(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

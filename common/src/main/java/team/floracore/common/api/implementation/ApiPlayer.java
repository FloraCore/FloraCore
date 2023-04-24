package team.floracore.common.api.implementation;

import org.floracore.api.player.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class ApiPlayer implements PlayerAPI {
    private final FloraCorePlugin plugin;

    public ApiPlayer(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public Players getPlayers(UUID uuid) {
        return plugin.getStorage().getImplementation().selectPlayers(uuid);
    }

    public Players getPlayers(String name) {
        return plugin.getStorage().getImplementation().selectPlayers(name);
    }

    @Override
    public String getPlayerRecordName(UUID uuid) {
        return getPlayers(uuid).getName();
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

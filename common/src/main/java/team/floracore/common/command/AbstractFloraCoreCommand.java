package team.floracore.common.command;

import team.floracore.common.plugin.*;

public abstract class AbstractFloraCoreCommand implements FloraCoreCommand {
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }
}

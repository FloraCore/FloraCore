package team.floracore.bungee.command;

import team.floracore.bungee.*;
import team.floracore.common.command.*;

public class FloraCoreBungeeCommand extends AbstractFloraCoreCommand {
    private final FCBungeePlugin plugin;

    public FloraCoreBungeeCommand(FCBungeePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public FCBungeePlugin getPlugin() {
        return plugin;
    }
}

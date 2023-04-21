package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.*;

import java.util.*;
import java.util.stream.*;

public abstract class AbstractFloraCoreCommand implements FloraCoreCommand {
    private final FloraCorePlugin plugin;

    public AbstractFloraCoreCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    @Suggestions("onlinePlayers")
    public @NonNull List<String> getOnlinePlayers(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return plugin.getBootstrap().getServer().getOnlinePlayers().stream().sorted(Comparator.comparing(Player::getDisplayName)).map(Player::getDisplayName).collect(Collectors.toList());
    }
}

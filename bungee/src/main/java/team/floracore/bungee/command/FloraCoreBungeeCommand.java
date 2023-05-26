package team.floracore.bungee.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.connection.*;
import org.jetbrains.annotations.*;
import team.floracore.bungee.*;
import team.floracore.common.command.*;

import java.util.*;
import java.util.stream.*;

public class FloraCoreBungeeCommand extends AbstractFloraCoreCommand {
    private final FCBungeePlugin plugin;

    public FloraCoreBungeeCommand(FCBungeePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public FCBungeePlugin getPlugin() {
        return plugin;
    }

    @Suggestions("onlinePlayers")
    public @NotNull List<String> getOnlinePlayers(final @NotNull CommandContext<CommandSender> sender,
                                                  final @NotNull String input) {
        CommandSender s = sender.getSender();
        ProxiedPlayer p = (ProxiedPlayer) s;
        return p.getServer().getInfo().getPlayers().stream()
                .sorted(Comparator.comparing(ProxiedPlayer::getName))
                .map(ProxiedPlayer::getName)
                .collect(Collectors.toList());
    }
}

package team.floracore.bukkit.command;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.comphenix.protocol.ProtocolManager;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.floracore.api.server.ServerType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.SERVER;
import team.floracore.common.util.CaffeineFactory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FloraCoreBukkitCommand extends AbstractFloraCoreCommand {
    private static final Cache<String, SERVER> serverCache = CaffeineFactory.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();
    private final FCBukkitPlugin plugin;

    public FloraCoreBukkitCommand(FCBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Suggestions("onlinePlayers")
    public @NotNull List<String> getOnlinePlayers(final @NotNull CommandContext<CommandSender> sender,
                                                  final @NotNull String input) {
        return plugin.getOnlineSenders()
                .sorted(Comparator.comparing(Sender::getDisplayName))
                .map(Sender::getDisplayName)
                .collect(Collectors.toList());
    }

    @Suggestions("worlds-all")
    public @NotNull List<String> getWorldsWithAll(final @NotNull CommandContext<CommandSender> sender,
                                                  final @NotNull String input) {
        final List<String> worlds = getWorlds(sender, input);
        worlds.add("*");
        return worlds;
    }

    @Suggestions("worlds")
    public @NotNull List<String> getWorlds(final @NotNull CommandContext<CommandSender> sender,
                                           final @NotNull String input) {
        final List<String> worlds = Lists.newArrayList();
        for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        return worlds;
    }

    @Suggestions("commonDurations")
    public @NotNull List<String> getCommonDurations(final @NotNull CommandContext<CommandSender> sender,
                                                    final @NotNull String input) {
        return ImmutableList.of("1", "60", "600", "3600", "86400");
    }

    public boolean whetherServerEnableAutoSync1() {
        return getServer().isAutoSync1();
    }

    public SERVER getServer() {
        String name = getPlugin().getServerName();
        SERVER server = serverCache.getIfPresent(name);
        if (server == null) {
            server = getStorageImplementation().selectServer(name);
            serverCache.put(name, server);
        }
        return server;
    }

    public FCBukkitPlugin getPlugin() {
        return plugin;
    }

    public boolean whetherServerEnableAutoSync2() {
        return getServer().isAutoSync2();
    }

    public ServerType getServerType() {
        return getServer().getType();
    }

    public ProtocolManager getProtocolManager() {
        return plugin.getProtocolManager();
    }
}

package team.floracore.bukkit.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.floracore.api.server.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.common.command.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class FloraCoreBukkitCommand extends AbstractFloraCoreCommand {
    private final FCBukkitPlugin plugin;
    private final AsyncCache<String, SERVER> serversCache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .maximumSize(10000)
            .buildAsync();

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

    @Suggestions("worlds")
    public @NotNull List<String> getWorlds(final @NotNull CommandContext<CommandSender> sender,
                                           final @NotNull String input) {
        final List<String> worlds = Lists.newArrayList();
        for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        return worlds;
    }

    @Suggestions("worlds-all")
    public @NotNull List<String> getWorldsWithAll(final @NotNull CommandContext<CommandSender> sender,
                                                  final @NotNull String input) {
        final List<String> worlds = Lists.newArrayList();
        for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        worlds.add("*");
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
        CompletableFuture<SERVER> servers = serversCache.get(name, getStorageImplementation()::selectServer);
        serversCache.put(name, servers);
        return servers.join();
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
}

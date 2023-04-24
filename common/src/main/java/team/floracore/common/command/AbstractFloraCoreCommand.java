package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.google.common.collect.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

public abstract class AbstractFloraCoreCommand implements FloraCoreCommand {
    private final FloraCorePlugin plugin;
    private final StorageImplementation storageImplementation;

    public AbstractFloraCoreCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
        storageImplementation = getPlugin().getStorage().getImplementation();
    }

    public static Duration parseDuration(String input) {
        try {
            long number = Long.parseLong(input);
            Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
            return checkPastDate(Duration.between(now, Instant.ofEpochSecond(number)));
        } catch (NumberFormatException e) {
            // ignore
        }

        try {
            return checkPastDate(DurationParser.parseDuration(input));
        } catch (IllegalArgumentException e) {
            // ignore
        }

        return null;
    }

    private static Duration checkPastDate(Duration duration) {
        if (duration.isNegative()) {
            throw new RuntimeException();
        }
        return duration;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    @Override
    @Suggestions("onlinePlayers")
    public @NonNull List<String> getOnlinePlayers(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return plugin.getBootstrap().getServer().getOnlinePlayers().stream().sorted(Comparator.comparing(Player::getDisplayName)).map(Player::getDisplayName).collect(Collectors.toList());
    }

    @Override
    @Suggestions("worlds")
    public @NonNull List<String> getWorlds(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        final List<String> worlds = Lists.newArrayList();
        for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        return worlds;
    }

    @Override
    @Suggestions("worlds-all")
    public @NonNull List<String> getWorldsWithAll(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        final List<String> worlds = Lists.newArrayList();
        for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        worlds.add("*");
        return worlds;
    }

    @Override
    @Suggestions("commonDurations")
    public @NonNull List<String> getCommonDurations(final @NonNull CommandContext<CommandSender> sender, final @NonNull String input) {
        return ImmutableList.of("1", "60", "600", "3600", "86400");
    }

    @Override
    public boolean whetherServerEnableAutoSync() {
        StorageImplementation storageImplementation = getPlugin().getStorage().getImplementation();
        Servers servers = storageImplementation.selectServers(getPlugin().getServerName());
        return servers.isAutoSync();
    }

    @Override
    public StorageImplementation getStorageImplementation() {
        return storageImplementation;
    }
}

package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import com.github.benmanes.caffeine.cache.*;
import com.google.common.collect.*;
import net.luckperms.api.*;
import net.luckperms.api.model.user.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public abstract class AbstractFloraCoreCommand implements FloraCoreCommand {
    private final FloraCorePlugin plugin;
    private final StorageImplementation storageImplementation;
    private final AsyncCache<String, SERVER> serversCache = Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(10000).buildAsync();
    private final Executor asyncExecutor;

    public final String EMPTY_DESCRIPTION = "%empty%";

    public AbstractFloraCoreCommand(FloraCorePlugin plugin) {
        this.plugin = plugin;
        this.storageImplementation = plugin.getStorage().getImplementation();
        this.asyncExecutor = plugin.getBootstrap().getScheduler().async();
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

    public SERVER getServer() {
        String name = getPlugin().getServerName();
        CompletableFuture<SERVER> servers = serversCache.get(name, storageImplementation::selectServer);
        serversCache.put(name, servers);
        return servers.join();
    }

    @Override
    public boolean whetherServerEnableAutoSync1() {
        return getServer().isAutoSync1();
    }

    @Override
    public boolean whetherServerEnableAutoSync2() {
        return getServer().isAutoSync2();
    }

    @Override
    public ServerType getServerType() {
        return getServer().getType();
    }

    @Override
    public StorageImplementation getStorageImplementation() {
        return storageImplementation;
    }

    @Override
    public Executor getAsyncExecutor() {
        return asyncExecutor;
    }

    @Override
    public CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        CompletableFuture<User> future = luckPerms.getUserManager().loadUser(uuid);
        return future.thenApply(user -> {
            if (user == null) {
                // 加载用户数据失败
                return false;
            }
            return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
        });
    }

    @Override
    public boolean hasPermission(UUID uuid, String permission) {
        return hasPermissionAsync(uuid, permission).join();
    }

    @Override
    public String getPlayerRecordName(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
    }

    @Override
    public boolean isOnline(UUID uuid) {
        return getPlugin().getApiProvider().getPlayerAPI().isOnline(uuid);
    }
}

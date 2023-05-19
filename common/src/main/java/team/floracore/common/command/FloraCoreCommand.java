package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.server.*;
import team.floracore.common.storage.implementation.*;

import java.util.*;
import java.util.concurrent.*;

public interface FloraCoreCommand {
    @Suggestions("onlinePlayers")
    @NonNull List<String> getOnlinePlayers(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("worlds")
    @NonNull List<String> getWorlds(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("worlds-all")
    @NonNull List<String> getWorldsWithAll(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("commonDurations")
    @NonNull List<String> getCommonDurations(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    boolean whetherServerEnableAutoSync1();

    boolean whetherServerEnableAutoSync2();

    ServerType getServerType();

    StorageImplementation getStorageImplementation();

    Executor getAsyncExecutor();

    CompletableFuture<Boolean> hasPermissionAsync(UUID uuid, String permission);

    boolean hasPermission(UUID uuid, String permission);

    String getPlayerRecordName(UUID uuid);

    boolean isOnline(UUID uuid);
}

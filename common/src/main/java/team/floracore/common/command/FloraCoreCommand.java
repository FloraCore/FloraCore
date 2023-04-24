package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.checkerframework.checker.nullness.qual.*;
import team.floracore.common.storage.implementation.*;

import java.util.*;

public interface FloraCoreCommand {
    @Suggestions("onlinePlayers")
    @NonNull List<String> getOnlinePlayers(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("worlds")
    @NonNull List<String> getWorlds(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("worlds-all")
    @NonNull List<String> getWorldsWithAll(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("commonDurations")
    @NonNull List<String> getCommonDurations(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    boolean whetherServerEnableAutoSync();

    StorageImplementation getStorageImplementation();
}

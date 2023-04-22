package team.floracore.common.command;

import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

public interface FloraCoreCommand {
    @Suggestions("onlinePlayers")
    @NonNull List<String> getOnlinePlayers(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    @Suggestions("commonDurations")
    @NonNull List<String> getCommonDurations(@NonNull CommandContext<CommandSender> sender, @NonNull String input);

    ItemStack getItemInMainHand(Player player);

    void setItemInMainHand(Player player, ItemStack stack);
}

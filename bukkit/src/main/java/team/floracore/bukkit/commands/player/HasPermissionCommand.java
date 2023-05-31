package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * HasPermission命令
 */
@CommandPermission("floracore.command.haspermission")
@CommandDescription("检查玩家是否拥有目标权限")
public class HasPermissionCommand extends FloraCoreBukkitCommand {
    public HasPermissionCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("haspermission <target> <permission>")
    @CommandDescription("检查玩家是否拥有目标权限")
    public void execute(@NotNull CommandSender s,
                        @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target,
                        @NotNull @Argument(value = "permission", suggestions = "permission_list") String permission) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
        if (ut == null) {
            MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
            return;
        }
        (hasPermission(ut,
                       permission) ? PlayerCommandMessage.COMMAND_HASPERMISSION_YES : PlayerCommandMessage.COMMAND_HASPERMISSION_NO).send(
                sender,
                target,
                permission);
    }

    @Suggestions("permission_list")
    public @NotNull List<String> getPermissionList(@NotNull CommandContext<CommandSender> sender,
                                                   @NotNull String input) {
        return Bukkit.getPluginManager()
                     .getPermissions()
                     .stream()
                     .collect(ArrayList::new, (list, element) -> list.add(element.getName()), ArrayList::addAll);
    }
}

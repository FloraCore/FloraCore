package team.floracore.paper.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.paper.*;
import team.floracore.paper.command.*;

import java.util.*;

/**
 * HasPermission命令
 */
@CommandDescription("检查玩家是否拥有目标权限")
@CommandPermission("floracore.command.haspermission")
public class HasPermissionCommand extends AbstractFloraCoreCommand {
    public HasPermissionCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandDescription("检查玩家是否拥有目标权限")
    @CommandMethod("haspermission <target> <permission>")
    public void execute(@NotNull CommandSender s, @NotNull @Argument(value = "target", suggestions = "onlinePlayers") String target, @NotNull @Argument(value = "permission", suggestions = "permission_list") String permission) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        UUID ut = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(target);
        if (ut == null) {
            MiscMessage.PLAYER_NOT_FOUND.send(sender, target);
            return;
        }
        (hasPermission(ut, permission) ? Message.COMMAND_HASPERMISSION_YES : Message.COMMAND_HASPERMISSION_NO).send(sender, target, permission);
    }

    @Suggestions("permission_list")
    public @NotNull List<String> getPermissionList(@NotNull CommandContext<CommandSender> sender, @NotNull String input) {
        return Bukkit.getPluginManager().getPermissions().stream().collect(ArrayList::new, (list, element) -> list.add(element.getName()), ArrayList::addAll);
    }
}

package team.floracore.common.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

@CommandDescription("检查玩家是否拥有目标权限")
@CommandPermission("floracore.command.haspermission")
public class HasPermissionCommand extends AbstractFloraCoreCommand {
    public HasPermissionCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandDescription("检查玩家是否拥有目标权限")
    @CommandMethod("haspermission <target> <permission>")
    public void execute(
            @NotNull CommandSender s,
            @NotNull @Argument("target") Player target,
            @NotNull @Argument("permission") String permission
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        (target.hasPermission(permission) ? Message.COMMAND_HASPERMISSION_YES : Message.COMMAND_HASPERMISSION_NO)
                .send(sender, target.getName(), permission);
    }
}

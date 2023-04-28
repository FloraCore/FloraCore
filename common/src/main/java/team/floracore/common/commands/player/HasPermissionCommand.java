package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

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

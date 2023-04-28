package team.floracore.common.commands.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.List;

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
            @NotNull @Argument(value = "permission", suggestions = "permission_list") String permission
    ) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        (target.hasPermission(permission) ? Message.COMMAND_HASPERMISSION_YES : Message.COMMAND_HASPERMISSION_NO)
                .send(sender, target.getName(), permission);
    }

    @Suggestions("permission_list")
    public @NotNull List<String> getPermissionList(@NotNull CommandContext<CommandSender> sender, @NotNull String input) {
        return Bukkit.getPluginManager().getPermissions()
                .stream().collect(ArrayList::new, (list, element) -> list.add(element.getName()), ArrayList::addAll);
    }
}

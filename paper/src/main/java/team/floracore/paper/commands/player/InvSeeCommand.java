package team.floracore.paper.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;
import team.floracore.paper.*;
import team.floracore.paper.command.*;

/**
 * InvSee命令
 */
@CommandPermission("floracore.command.invsee")
@CommandDescription("打开指定玩家的物品栏")
public class InvSeeCommand extends AbstractFloraCoreCommand {
    public InvSeeCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("invsee|inv <target>")
    public void invsee(final @NotNull Player s, final @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (s.getUniqueId() == target.getUniqueId()) {
            Message.COMMAND_INVSEE_SELF.send(sender);
            return;
        }
        s.openInventory(target.getInventory());
        Message.COMMAND_INVSEE.send(sender, target.getDisplayName());
    }
}

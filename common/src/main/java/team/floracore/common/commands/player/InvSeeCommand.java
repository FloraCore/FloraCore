package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

@CommandPermission("floracore.command.invsee")
public class InvSeeCommand extends AbstractFloraCoreCommand {
    public InvSeeCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("invsee|inv <target>")
    @CommandDescription("打开指定玩家的物品栏")
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

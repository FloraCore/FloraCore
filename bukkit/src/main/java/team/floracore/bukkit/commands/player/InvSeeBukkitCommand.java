package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.common.sender.*;

/**
 * InvSee命令
 */
@CommandPermission("floracore.command.invsee")
@CommandDescription("打开指定玩家的物品栏")
public class InvSeeBukkitCommand extends FloraCoreBukkitCommand {
    public InvSeeBukkitCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("invsee|inv <target>")
    public void invsee(final @NotNull Player s, final @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (s.getUniqueId() == target.getUniqueId()) {
            PlayerCommandMessage.COMMAND_INVSEE_SELF.send(sender);
            return;
        }
        s.openInventory(target.getInventory());
        PlayerCommandMessage.COMMAND_INVSEE.send(sender, target.getDisplayName());
    }
}

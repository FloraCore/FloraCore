package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;

/**
 * InvSee命令
 */
@CommandDescription("floracore.command.description.invsee")
@CommandPermission("floracore.command.invsee")
public class InvSeeCommand extends FloraCoreBukkitCommand {
    public InvSeeCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("invsee|inv <target>")
    @CommandDescription("floracore.command.description.invsee")
    public void invSee(final @NotNull Player s, final @Argument("target") Player target) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        if (s.getUniqueId() == target.getUniqueId()) {
            PlayerCommandMessage.COMMAND_INVSEE_SELF.send(sender);
            return;
        }
        s.openInventory(target.getInventory());
        PlayerCommandMessage.COMMAND_INVSEE.send(sender, target.getDisplayName());
    }
}

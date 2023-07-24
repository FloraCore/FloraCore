package team.floracore.bukkit.command.impl.server;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.ServerCommandMessage;
import team.floracore.common.sender.Sender;

/**
 * @author xLikeWATCHDOG
 */
@CommandPermission("floracore.command.bungeecommand")
public class BungeeCommand extends FloraCoreBukkitCommand {
    public BungeeCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("bungeecommand|bcmd <command>")
    public void bungeeCommand(@NotNull CommandSender s, @NotNull @Argument("command") @Greedy String command) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        getPlugin().getBukkitMessagingFactory().bungeeCommand(command);
        ServerCommandMessage.COMMAND_BUNGEE_COMMAND.send(sender, sender.getName(), command);
    }
}

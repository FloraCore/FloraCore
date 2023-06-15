package team.floracore.bukkit.command.impl.server;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.ServerCommandMessage;
import team.floracore.common.locale.message.AbstractMessage;
import team.floracore.common.sender.Sender;

/**
 * BroadCast命令
 */
@CommandDescription("floracore.command.description.broadcast")
@CommandPermission("floracore.command.broadcast")
public class BroadCastCommand extends FloraCoreBukkitCommand {
    public BroadCastCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("broadcast|bc <contents>")
    @CommandDescription("floracore.command.description.broadcast")
    public void broadcast(@NotNull CommandSender s, @NotNull @Argument("contents") @Greedy String contents) {
        Sender consoleSender = getPlugin().getConsoleSender();
        AbstractMessage.Args1<String> message = ServerCommandMessage.COMMAND_BROADCAST;
        message.send(consoleSender, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            message.send(sender, contents);
        }
    }

    @CommandMethod("notice <contents>")
    @CommandDescription("floracore.command.description.broadcast")
    public void broadcastW0(@NotNull CommandSender s, @NotNull @Argument("contents") @Greedy String contents) {
        Sender consoleSender = getPlugin().getConsoleSender();
        AbstractMessage.Args1<String> message = ServerCommandMessage.COMMAND_BROADCAST_WITHOUT_PREFIX;
        message.send(consoleSender, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            message.send(sender, contents);
        }
    }

    @CommandMethod("nbc <contents>")
    @CommandDescription("floracore.command.description.broadcast")
    public void broadcastW1(@NotNull CommandSender s, @NotNull @Argument("contents") @Greedy String contents) {
        Sender consoleSender = getPlugin().getConsoleSender();
        AbstractMessage.Args1<String> message = ServerCommandMessage.COMMAND_BROADCAST_WITHOUT_PREFIX;
        message.send(consoleSender, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender sender = getPlugin().getSenderFactory().wrap(player);
            message.send(sender, contents);
        }
    }
}

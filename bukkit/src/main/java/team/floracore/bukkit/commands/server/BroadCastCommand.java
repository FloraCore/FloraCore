package team.floracore.bukkit.commands.server;

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
import team.floracore.common.sender.Sender;

/**
 * BroadCast命令
 */
@CommandPermission("floracore.command.broadcast")
@CommandDescription("在服务器发送广播信息")
public class BroadCastCommand extends FloraCoreBukkitCommand {
    public BroadCastCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("broadcast|bc <contents>")
    public void broadcast(@NotNull CommandSender s, @NotNull @Argument("contents") @Greedy String contents) {
        Sender s1 = getPlugin().getConsoleSender();
        ServerCommandMessage.COMMAND_BROADCAST.send(s1, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            ServerCommandMessage.COMMAND_BROADCAST.send(s2, contents);
        }
    }
}

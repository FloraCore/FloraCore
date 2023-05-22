package team.floracore.bukkit.commands.server;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.common.sender.*;

/**
 * BroadCast命令
 */
@CommandPermission("floracore.command.broadcast")
@CommandDescription("在服务器发送广播信息")
public class BroadCastCommand extends AbstractFloraCoreCommand {
    public BroadCastCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("broadcast|bc <contents>")
    public void broadcast(@NotNull CommandSender s, @NonNull @Argument("contents") @Greedy String contents) {
        Sender s1 = getPlugin().getConsoleSender();
        ServerCommandMessage.COMMAND_BROADCAST.send(s1, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            ServerCommandMessage.COMMAND_BROADCAST.send(s2, contents);
        }
    }
}

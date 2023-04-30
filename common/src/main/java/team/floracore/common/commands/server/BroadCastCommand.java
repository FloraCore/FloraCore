package team.floracore.common.commands.server;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.specifier.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

/**
 * BroadCast命令
 */
@CommandPermission("floracore.command.broadcast")
public class BroadCastCommand extends AbstractFloraCoreCommand {
    public BroadCastCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("broadcast|bc <contents>")
    @CommandDescription("喂饱您自己")
    public void broadcast(@NotNull CommandSender s, @NonNull @Argument("contents") @Greedy String contents) {
        Sender s1 = getPlugin().getConsoleSender();
        Message.COMMAND_BROADCAST.send(s1, contents);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            Message.COMMAND_BROADCAST.send(s2, contents);
        }
    }
}

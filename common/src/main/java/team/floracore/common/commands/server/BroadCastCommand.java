package team.floracore.common.commands.server;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

public class BroadCastCommand extends AbstractFloraCoreCommand {
    public BroadCastCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("broadcast|bc <contents>")
    @CommandDescription("喂饱您自己")
    public void broadcast(@NotNull CommandSender s, @NonNull @Argument("contents") String[] contents) {
        Sender s1 = getPlugin().getConsoleSender();
        StringBuilder ret = new StringBuilder();
        for (String content : contents) {
            ret.append(content);
            ret.append(" ");
        }
        Message.COMMAND_BROADCAST.send(s1, ret.toString());
        for (Player player : Bukkit.getOnlinePlayers()) {
            Sender s2 = getPlugin().getSenderFactory().wrap(player);
            Message.COMMAND_BROADCAST.send(s2, ret.toString());
        }
    }
}

package team.floracore.common.commands.player;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.sender.Sender;

import java.util.Set;

@CommandDescription("列出本服所有拥有OP权限的玩家")
@CommandPermission("floracore.command.oplist")
public class OPListCommand extends AbstractFloraCoreCommand {
    public OPListCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandDescription("列出本服所有拥有OP权限的玩家")
    @CommandMethod("oplist")
    public void execute(@NotNull CommandSender s) {
        Sender sender = getPlugin().getSenderFactory().wrap(s);
        Set<OfflinePlayer> ops = Bukkit.getOperators();
        if (ops.isEmpty()) {
            Message.COMMAND_OPLIST_HEADER_NONE.send(sender);
            return;
        }
        Message.COMMAND_OPLIST_HEADER.send(sender, ops.size());
        ops.forEach(op -> Message.COMMAND_OPLIST_ENTRY.send(sender, op.getName(), op.getUniqueId(), op.isOnline()));
    }
}

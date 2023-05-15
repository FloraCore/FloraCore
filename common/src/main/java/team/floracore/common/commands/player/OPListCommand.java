package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import java.util.*;

/**
 * OPList命令
 */
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

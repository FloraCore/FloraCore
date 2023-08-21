package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;

import java.util.Set;

/**
 * OPList命令
 */
@CommandDescription("floracore.command.description.oplist")
@CommandPermission("floracore.command.oplist")
public class OPListCommand extends FloraCoreBukkitCommand {
	public OPListCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("oplist")
	@CommandDescription("floracore.command.description.oplist")
	public void execute(@NotNull CommandSender s) {
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		Set<OfflinePlayer> ops = Bukkit.getOperators();
		if (ops.isEmpty()) {
			PlayerCommandMessage.COMMAND_OPLIST_HEADER_NONE.send(sender);
			return;
		}
		PlayerCommandMessage.COMMAND_OPLIST_HEADER.send(sender, ops.size());
		ops.forEach(op -> PlayerCommandMessage.COMMAND_OPLIST_ENTRY.send(sender,
				op.getName(),
				op.getUniqueId(),
				op.isOnline()));
	}
}

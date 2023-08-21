package team.floracore.bukkit.command.impl.server;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.specifier.Greedy;
import org.bukkit.command.CommandSender;
import org.floracore.api.data.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.DATA;

import java.util.UUID;

/**
 * KickFromBungee命令
 */
@CommandDescription("floracore.command.description.kick-from-bungee")
@CommandPermission("floracore.command.kick-from-bungee")
public class KickFromBungeeCommand extends FloraCoreBukkitCommand {
	public KickFromBungeeCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("kickfrombungee|kick-from-bungee <player> [reason]")
	@CommandDescription("floracore.command.description.kick-from-bungee")
	public void kickPlayerFromBungee(@NotNull CommandSender s, @Argument("player") @NotNull String playerName, @Nullable @Greedy @Argument("reason") String reason) {
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		UUID targetUUID = getPlugin().getApiProvider().getPlayerAPI().getPlayerRecordUUID(playerName);
		if (targetUUID == null) {
			MiscMessage.PLAYER_NOT_FOUND.send(sender, playerName);
			return;
		}
		DATA data = getStorageImplementation().getSpecifiedData(targetUUID, DataType.FUNCTION, "server-status");
		if (data == null) {
			MiscMessage.PLAYER_NOT_FOUND.send(sender, playerName);
			return;
		}
		if (reason == null) {
			reason = "&fKicked by an operator.";
		}
		getPlugin().getBukkitMessagingFactory().submitKick(targetUUID, reason);
	}
}

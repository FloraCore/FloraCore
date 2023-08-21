package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.PLAYER;

import java.util.UUID;

/**
 * RealName命令
 */
@CommandDescription("floracore.command.description.realname")
@CommandPermission("floracore.command.realname")
public class RealNameCommand extends FloraCoreBukkitCommand {
	public RealNameCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("realname <target>")
	@CommandDescription("floracore.command.description.realname")
	public void realName(final @NotNull Player s, final @Argument("target") Player target) {
		Sender sender = getPlugin().getSenderFactory().wrap(s);
		UUID tu = target.getUniqueId();
		PLAYER ps = getStorageImplementation().selectPlayer(tu);
		String name = target.getDisplayName();
		String realName = ps.getName();
		PlayerCommandMessage.COMMAND_REALNAME_SUCCESS.send(sender, name, realName);
	}
}

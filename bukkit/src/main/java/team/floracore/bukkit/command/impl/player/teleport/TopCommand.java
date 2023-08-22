package team.floracore.bukkit.command.impl.player.teleport;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.util.LocationUtil;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

/**
 * Top命令
 */
@CommandPermission("floracore.command.top")
@CommandDescription("floracore.command.description.top")
public class TopCommand extends FloraCoreBukkitCommand {
	public TopCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("top")
	@CommandDescription("floracore.command.description.top.do")
	public void top(final @NotNull Player p) {
		Sender sender = getPlugin().getSenderFactory().wrap(p);
		final int topX = p.getLocation().getBlockX();
		final int topZ = p.getLocation().getBlockZ();
		final float pitch = p.getLocation().getPitch();
		final float yaw = p.getLocation().getYaw();
		final Location unsafe = new Location(p.getWorld(), topX, p.getWorld().getMaxHeight(), topZ, yaw, pitch);
		try {
			final Location safe = LocationUtil.getSafeDestination(unsafe);
			PaperLib.teleportAsync(p, safe, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
				if (result) {
					PlayerCommandMessage.COMMAND_TELEPORT_TOP.send(sender);
				} else {
					MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
				}
			});
		} catch (Exception e) {
			MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
		}
	}
}
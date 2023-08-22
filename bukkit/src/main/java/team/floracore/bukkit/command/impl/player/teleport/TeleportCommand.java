package team.floracore.bukkit.command.impl.player.teleport;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Flag;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.config.features.FeaturesKeys;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.util.LocationUtil;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 普通的TP命令
 *
 * @author xLikeWATCHDOG
 */
@CommandDescription("floracore.command.description.teleport")
@CommandPermission("floracore.command.teleport")
public class TeleportCommand extends FloraCoreBukkitCommand {
	public TeleportCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("teleport|tp <target>")
	@CommandDescription("floracore.command.description.teleport.self")
	@CommandPermission("floracore.command.teleport")
	public void teleport(final @NotNull Player sender,
	                     final @NotNull @Argument("target") Player target) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		Location location = target.getLocation();
		PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
		if (getPlugin().getFeaturesConfiguration().get(FeaturesKeys.TELEPORT_SAFETY)) {
			try {
				location = LocationUtil.getSafeDestination(location);
			} catch (Exception e) {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
				return;
			}
		}
		PaperLib.teleportAsync(sender, location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
			if (result) {
				PlayerCommandMessage.COMMAND_TELEPORT.send(s, target.getDisplayName());
			} else {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
			}
		});
	}

	@CommandMethod("teleport|tp <target> <transmitter>")
	@CommandDescription("floracore.command.description.teleport.other")
	@CommandPermission("floracore.command.teleport.other")
	public void teleportOther(final @NotNull Player sender,
	                          final @NotNull @Argument("target") Player target,
	                          final @NotNull @Argument("transmitter") Player transmitter,
	                          final @Nullable @Flag("silent") Boolean silent) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		Sender t = getPlugin().getSenderFactory().wrap(target);
		Location location = transmitter.getLocation();
		PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
		if (getPlugin().getFeaturesConfiguration().get(FeaturesKeys.TELEPORT_SAFETY)) {
			try {
				location = LocationUtil.getSafeDestination(location);
			} catch (Exception e) {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
				return;
			}
		}
		PaperLib.teleportAsync(target, location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
			if (result) {
				PlayerCommandMessage.COMMAND_TELEPORT_OTHER_SENDER.send(s, target.getDisplayName(), transmitter.getDisplayName());
				if (silent == null || !silent) {
					PlayerCommandMessage.COMMAND_TELEPORT_OTHER.send(t, sender.getDisplayName(), transmitter.getDisplayName());
				}
			} else {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
			}
		});
	}

	@CommandMethod("teleportposition|tpp <x> <y> <z>")
	@CommandDescription("floracore.command.description.teleport.position.self")
	@CommandPermission("floracore.command.teleport.position")
	public void teleportPosition(final @NotNull Player sender,
	                             final @NotNull @Argument(value = "x", suggestions = "positions") String x,
	                             final @NotNull @Argument(value = "y", suggestions = "positions") String y,
	                             final @NotNull @Argument(value = "z", suggestions = "positions") String z) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		double x2 = parseCoordinate(x, sender.getLocation().getX());
		double y2 = parseCoordinate(y, sender.getLocation().getY());
		double z2 = parseCoordinate(z, sender.getLocation().getZ());
		if (x2 > 30000000 || y2 > 30000000 || z2 > 30000000 || x2 < -30000000 || y2 < -30000000 || z2 < -30000000) {
			PlayerCommandMessage.COMMAND_TELEPORT_INVALID_SCOPE.send(s);
			return;
		}
		Location locpos = new Location(sender.getWorld(), x2, y2, z2, sender.getLocation().getYaw(), sender.getLocation().getPitch());
		PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
		if (getPlugin().getFeaturesConfiguration().get(FeaturesKeys.TELEPORT_SAFETY)) {
			try {
				locpos = LocationUtil.getSafeDestination(locpos);
			} catch (Exception e) {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
				return;
			}
		}
		PaperLib.teleportAsync(sender, locpos, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
			if (result) {
				PlayerCommandMessage.COMMAND_TELEPORT_POSITION.send(s, x2, y2, z2);
			} else {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
			}
		});
	}

	@CommandMethod("teleportposition|tpp <x> <y> <z> <transmitter>")
	@CommandDescription("floracore.command.description.teleport.position.other")
	@CommandPermission("floracore.command.teleport.position.other")
	public void teleportPositionOther(final @NotNull Player sender,
	                                  final @NotNull @Argument(value = "x", suggestions = "positions") String x,
	                                  final @NotNull @Argument(value = "y", suggestions = "positions") String y,
	                                  final @NotNull @Argument(value = "z", suggestions = "positions") String z,
	                                  final @NotNull @Argument("transmitter") Player transmitter,
	                                  final @Nullable @Flag("silent") Boolean silent) {
		Sender s = getPlugin().getSenderFactory().wrap(sender);
		Sender t = getPlugin().getSenderFactory().wrap(transmitter);
		double x2 = parseCoordinate(x, transmitter.getLocation().getX());
		double y2 = parseCoordinate(y, transmitter.getLocation().getY());
		double z2 = parseCoordinate(z, transmitter.getLocation().getZ());
		if (x2 > 30000000 || y2 > 30000000 || z2 > 30000000 || x2 < -30000000 || y2 < -30000000 || z2 < -30000000) {
			PlayerCommandMessage.COMMAND_TELEPORT_INVALID_SCOPE.send(s);
			return;
		}
		Location locpos = new Location(transmitter.getWorld(), x2, y2, z2, transmitter.getLocation().getYaw(), transmitter.getLocation().getPitch());
		PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
		if (getPlugin().getFeaturesConfiguration().get(FeaturesKeys.TELEPORT_SAFETY)) {
			try {
				locpos = LocationUtil.getSafeDestination(locpos);
			} catch (Exception e) {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
				return;
			}
		}
		PaperLib.teleportAsync(transmitter, locpos, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
			if (result) {
				PlayerCommandMessage.COMMAND_TELEPORT_POSITION_OTHER.send(s, x2, y2, z2, transmitter.getDisplayName());
				if (silent == null || !silent) {
					PlayerCommandMessage.COMMAND_TELEPORT_POSITION_OTHER.send(t, x2, y2, z2, sender.getDisplayName());
				}
			} else {
				MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(s);
			}
		});
	}

	@Suggestions("positions")
	public List<String> getPositions(final @NotNull CommandContext<CommandSender> sender,
	                                 final @NotNull String input) {
		return new ArrayList<>(Arrays.asList("~", "0", "255"));
	}

	private double parseCoordinate(String coordinate, double defaultValue) {
		if (coordinate.startsWith("~")) {
			double relativeOffset = (coordinate.length() > 1) ? Double.parseDouble(coordinate.substring(1)) : 0;
			return defaultValue + relativeOffset;
		} else {
			return Double.parseDouble(coordinate);
		}
	}
}

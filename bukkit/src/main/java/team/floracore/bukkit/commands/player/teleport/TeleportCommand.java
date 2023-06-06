package team.floracore.bukkit.commands.player.teleport;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 普通的TP命令
 *
 * @author xLikeWATCHDOG
 */
@CommandPermission("floracore.command.teleport")
@CommandDescription("floracore.command.description.teleport")
public class TeleportCommand extends FloraCoreBukkitCommand {
    public TeleportCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("teleport|tp <target>")
    @CommandPermission("floracore.command.tp")
    public void teleport(final @NotNull Player sender,
                         final @NotNull @Argument("target") Player target) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Location location = target.getLocation();
        PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
        sender.teleport(location);
        PlayerCommandMessage.COMMAND_TELEPORT.send(s, target.getDisplayName());
    }

    @CommandMethod("teleport|tp <target> <transmitter>")
    @CommandPermission("floracore.command.tp.other")
    public void teleportOther(final @NotNull Player sender,
                              final @NotNull @Argument("target") Player target,
                              final @NotNull @Argument("transmitter") Player transmitter,
                              final @Nullable @Flag("silent") Boolean silent) {
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Sender t = getPlugin().getSenderFactory().wrap(transmitter);
        Location location = target.getLocation();
        PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
        transmitter.teleport(location);
        PlayerCommandMessage.COMMAND_TELEPORT_OTHER_SENDER.send(s, transmitter.getDisplayName(), target.getDisplayName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_TELEPORT_OTHER.send(t, sender.getDisplayName(), target.getDisplayName());
        }
    }

    @CommandMethod("teleportposition|tpp <x> <y> <z>")
    @CommandPermission("floracore.command.tp.position")
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
        final Location locpos = new Location(sender.getWorld(), x2, y2, z2, sender.getLocation().getYaw(), sender.getLocation().getPitch());
        PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
        sender.teleport(locpos);
        PlayerCommandMessage.COMMAND_TELEPORT_POSITION.send(s, x2, y2, z2);
    }

    @CommandMethod("teleportposition|tpp <x> <y> <z> <transmitter>")
    @CommandPermission("floracore.command.tp.position.other")
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
        final Location locpos = new Location(transmitter.getWorld(), x2, y2, z2, transmitter.getLocation().getYaw(), transmitter.getLocation().getPitch());
        PlayerCommandMessage.COMMAND_TELEPORT_TELEPORTING.send(s);
        transmitter.teleport(locpos);
        PlayerCommandMessage.COMMAND_TELEPORT_POSITION_OTHER.send(s, x2, y2, z2, transmitter.getDisplayName());
        if (silent == null || !silent) {
            PlayerCommandMessage.COMMAND_TELEPORT_POSITION_OTHER.send(t, x2, y2, z2, sender.getDisplayName());
        }
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

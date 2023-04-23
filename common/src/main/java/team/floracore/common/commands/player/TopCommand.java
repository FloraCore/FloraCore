package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.util.*;

@CommandPermission("floracore.command.top")
public class TopCommand extends AbstractFloraCoreCommand {
    public TopCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("top")
    @CommandDescription("传送至你当前位置的最高点")
    public void top(final @NotNull Player p) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        final int topX = p.getLocation().getBlockX();
        final int topZ = p.getLocation().getBlockZ();
        final float pitch = p.getLocation().getPitch();
        final float yaw = p.getLocation().getYaw();
        final Location unsafe = new Location(p.getWorld(), topX, p.getWorld().getMaxHeight(), topZ, yaw, pitch);
        try {
            final Location safe = LocationUtil.getSafeDestination(unsafe);
            p.teleport(safe);
            Message.COMMAND_TELEPORT_TOP.send(sender);
        } catch (Exception e) {
            Message.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
        }
    }
}

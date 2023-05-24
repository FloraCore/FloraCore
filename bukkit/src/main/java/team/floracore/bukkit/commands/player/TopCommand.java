package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.bukkit.*;
import team.floracore.bukkit.command.*;
import team.floracore.bukkit.locale.message.commands.*;
import team.floracore.bukkit.util.*;
import team.floracore.common.locale.message.*;
import team.floracore.common.sender.*;

/**
 * Top命令
 */
@CommandPermission("floracore.command.top")
@CommandDescription("传送至玩家当前位置的最高点")
public class TopCommand extends FloraCoreBukkitCommand {
    public TopCommand(FCBukkitPlugin plugin) {
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
            PlayerCommandMessage.COMMAND_TELEPORT_TOP.send(sender);
        } catch (Exception e) {
            MiscMessage.COMMAND_MISC_EXECUTE_COMMAND_EXCEPTION.send(sender);
        }
    }
}
package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.floracore.api.data.DataType;
import team.floracore.common.command.*;
import team.floracore.common.locale.Message;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.Sender;
import team.floracore.common.storage.misc.floracore.tables.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

@CommandPermission("floracore.command.speed")
public class SpeedCommand extends AbstractFloraCoreCommand implements Listener {
    public SpeedCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("setspeed|speed <type> <speed> [target]")
    @CommandDescription("修改玩家的移动/飞行速度")
    public void setSpeed(final @Nonnull CommandSender commandSender, @Nonnull @Argument("type") String type, @Nonnull @Argument("speed") String speed, @Nullable @Argument("target") String target, final @Nullable @Flag("silent") Boolean silent) {
        if (target == null) {
            if (commandSender instanceof Player) {
                checkSyntaxAndExecute((Player) commandSender, type, speed, null, false);
            } else {
                commandSender.sendMessage(ChatColor.RED + "Usage: /speed <type> <speed> <target>");
            }
        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                Sender sender = getPlugin().getSenderFactory().wrap(commandSender);
                Message.PLAYER_NOT_FOUND.send(sender, target);
                return;
            }
            checkSyntaxAndExecute(player, type, speed, commandSender, Boolean.TRUE.equals(silent));
        }
    }

    private void checkSyntaxAndExecute(Player target, String type, String speed, CommandSender sender, boolean silent) {
        float targetSpeed;
        try {
            targetSpeed = Float.parseFloat(speed);
        } catch (NumberFormatException e) {
            Message.COMMAND_INVALID_NUMBER.send(getPlugin().getSenderFactory().wrap(sender == null ? target : sender), speed);
            return;
        }
        boolean modified = false;
        if (type.equalsIgnoreCase("fly") || type.equalsIgnoreCase("flight") || type.equalsIgnoreCase("all") || type.equalsIgnoreCase("general")) {
            setSpeed(target, Type.FLY, targetSpeed, sender, silent);
            modified = true;
        }
        if (type.equalsIgnoreCase("walk") || type.equalsIgnoreCase("move") || type.equalsIgnoreCase("all") || type.equalsIgnoreCase("general")) {
            setSpeed(target, Type.WALK, targetSpeed, sender, silent);
            modified = true;
        }
        if (!modified) {
            Message.COMMAND_SPEED_NO_SUCH.send(getPlugin().getSenderFactory().wrap(sender == null ? target : sender), type);
        }
    }

    private void setSpeed(@Nonnull Player target, @Nonnull Type type, float speed, @Nullable CommandSender sender, boolean silent) {
        switch (type) {
            case FLY -> {
                target.setFlySpeed(speed);
                if (sender == null) {
                    Message.COMMAND_SPEED_OTHER.send(getPlugin().getSenderFactory().wrap(target), "", "飞行", String.valueOf(speed));
                } else {
                    Message.COMMAND_SPEED.send(getPlugin().getSenderFactory().wrap(sender), target.getName(), "飞行", String.valueOf(speed));
                    if (!silent) {
                        Message.COMMAND_SPEED_OTHER.send(getPlugin().getSenderFactory().wrap(target), sender.getName(), "飞行", String.valueOf(speed));
                    }
                }
            }
            case WALK -> {
                target.setWalkSpeed(speed);
                if (sender == null) {
                    Message.COMMAND_SPEED_OTHER.send(getPlugin().getSenderFactory().wrap(target), "", "移动", String.valueOf(speed));
                } else {
                    Message.COMMAND_SPEED.send(getPlugin().getSenderFactory().wrap(sender), target.getName(), "移动", String.valueOf(speed));
                    if (!silent) {
                        Message.COMMAND_SPEED_OTHER.send(getPlugin().getSenderFactory().wrap(target), sender.getName(), "移动", String.valueOf(speed));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID u = p.getUniqueId();
        if (whetherServerEnableAutoSync1()) {
            Data data = getStorageImplementation().getSpecifiedData(u, DataType.AUTO_SYNC, "fly-speed");
            if (data != null) {
                String value = data.getValue();
                float flySpeed = Float.parseFloat(value);
                p.setFlySpeed(flySpeed);
            }
            data = getStorageImplementation().getSpecifiedData(u, DataType.AUTO_SYNC, "walk-speed");
            if (data != null) {
                String value = data.getValue();
                float walkSpeed = Float.parseFloat(value);
                p.setWalkSpeed(walkSpeed);
            }
        }
    }

    enum Type {
        FLY,
        WALK
    }
}

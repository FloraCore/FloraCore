package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.*;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.command.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import javax.annotation.*;
import java.util.*;

/**
 * Speed命令
 */
@CommandPermission("floracore.command.speed")
public class SpeedCommand extends AbstractFloraCoreCommand implements Listener {
    public SpeedCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("setspeed|speed <type> <speed> [target]")
    @CommandDescription("修改玩家的移动/飞行速度")
    public void setSpeed(
            final @NotNull CommandSender commandSender,
            @NotNull @Argument(value = "type", suggestions = "get_types") String type,
            @NotNull @Argument("speed") String speed,
            @Nullable @Argument("target") String target,
            final @Nullable @Flag("silent") Boolean silent
    ) {
        if (target == null) {
            if (commandSender instanceof Player) {
                checkSyntaxAndExecute((Player) commandSender, type, speed, null, false);
            } else {
                Sender sender = getPlugin().getSenderFactory().wrap(commandSender);
                Message.COMMAND_INVALID_COMMAND_SENDER.send(sender, commandSender.getClass().getSimpleName(), Player.class.getSimpleName());
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
            Message.COMMAND_MISC_INVALID_NUMBER.send(getPlugin().getSenderFactory().wrap(sender == null ? target : sender), speed);
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
        Sender s = getPlugin().getSenderFactory().wrap(sender);
        Sender t = getPlugin().getSenderFactory().wrap(target);
        switch (type) {
            case FLY:
                target.setFlySpeed(speed);
                Component fly = Message.COMMAND_MISC_SPEED_FLY.build();
                if (sender == null) {
                    Message.COMMAND_SPEED.send(t, t.getDisplayName(), fly, String.valueOf(speed));
                } else {
                    Message.COMMAND_SPEED.send(s, t.getDisplayName(), fly, String.valueOf(speed));
                    if (!silent) {
                        Message.COMMAND_SPEED_OTHER.send(t, s.getDisplayName(), fly, String.valueOf(speed));
                    }
                }
                break;
            case WALK:
                target.setWalkSpeed(speed);
                Component walk = Message.COMMAND_MISC_SPEED_WALK.build();
                if (sender == null) {
                    Message.COMMAND_SPEED.send(t, t.getDisplayName(), walk, String.valueOf(speed));
                } else {
                    Message.COMMAND_SPEED.send(s, t.getDisplayName(), walk, String.valueOf(speed));
                    if (!silent) {
                        Message.COMMAND_SPEED_OTHER.send(t, s.getDisplayName(), walk, String.valueOf(speed));
                    }
                }
                break;
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

    @Suggestions("get_types")
    public List<String> getTypes(@NotNull CommandContext<CommandSender> sender, @NotNull String input) {
        return new ArrayList<>(Arrays.asList("fly", "walk"));
    }
}

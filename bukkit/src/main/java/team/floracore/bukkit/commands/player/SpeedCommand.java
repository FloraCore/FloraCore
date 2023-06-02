package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.common.config.ConfigKeys;
import team.floracore.common.locale.message.MiscMessage;
import team.floracore.common.sender.Sender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Speed命令
 */
@CommandPermission("floracore.command.speed")
@CommandDescription("设置玩家的指定类型的速度")
public class SpeedCommand extends FloraCoreBukkitCommand {
    public SpeedCommand(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @CommandMethod("speed <speed>")
    @CommandDescription("设置你当前状态的速度")
    public void speed(final @NotNull Player p, final @Argument(value = "speed", suggestions = "speeds") float speed) {
        boolean isFlying = p.isFlying();
        speedAdvance(p, speed, isFlying ? SpeedType.FLY.getName() : SpeedType.WALK.getName(), null, null);
    }

    @CommandMethod("speed <speed> <type> [target]")
    @CommandDescription("设置你（或指定玩家）的指定类型的速度")
    public void speedAdvance(final @NotNull Player p,
                             final @Argument(value = "speed", suggestions = "speeds") float speed,
                             final @Argument(value = "type", suggestions = "types") String type,
                             final @Argument("target") Player target,
                             final @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        try {
            final SpeedType speedType = SpeedType.parseSpeedType(type);
            final boolean isBypass = p.hasPermission("floracore.command.speed.bypass");
            if (target != null) {
                if (!p.hasPermission("floracore.command.speed.other")) {
                    MiscMessage.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
                    return;
                }
                switch (speedType) {
                    case FLY:
                        target.setFlySpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                    case WALK:
                        target.setWalkSpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                }
                if (!(silent != null && silent)) {
                    PlayerCommandMessage.COMMAND_SPEED_OTHER.send(sender,
                            p.getDisplayName(),
                            speedType == SpeedType.FLY ? PlayerCommandMessage.COMMAND_MISC_SPEED_FLY.build() : PlayerCommandMessage.COMMAND_MISC_SPEED_WALK.build(),
                            String.valueOf(speed));
                }
                PlayerCommandMessage.COMMAND_SPEED.send(sender,
                        target.getDisplayName(),
                        speedType == SpeedType.FLY ? PlayerCommandMessage.COMMAND_MISC_SPEED_FLY.build() : PlayerCommandMessage.COMMAND_MISC_SPEED_WALK.build(),
                        String.valueOf(speed));
            } else {
                switch (speedType) {
                    case FLY:
                        p.setFlySpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                    case WALK:
                        p.setWalkSpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                }
                PlayerCommandMessage.COMMAND_SPEED.send(sender,
                        p.getDisplayName(),
                        speedType == SpeedType.FLY ? PlayerCommandMessage.COMMAND_MISC_SPEED_FLY.build() : PlayerCommandMessage.COMMAND_MISC_SPEED_WALK.build(),
                        String.valueOf(speed));
            }
        } catch (IllegalArgumentException e) {
            PlayerCommandMessage.COMMAND_SPEED_NO_SUCH.send(sender, type);
        }
    }

    private float getRealMoveSpeed(final float userSpeed, final SpeedType speedType, final boolean isBypass) {
        final float defaultSpeed = speedType == SpeedType.FLY ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (!isBypass) {
            maxSpeed = (speedType == SpeedType.FLY ? getPlugin().getConfiguration()
                    .get(ConfigKeys.SPEED_MAX_FLY_SPEED) : getPlugin().getConfiguration()
                    .get(ConfigKeys.SPEED_MAX_WALK_SPEED)).floatValue();
        }

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            final float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }

    @Suggestions("speeds")
    public List<String> getSpeeds(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        return new ArrayList<>(Arrays.asList("1", "1.5", "1.75", "2"));
    }

    @Suggestions("types")
    public List<String> getTypes(final @NotNull CommandContext<CommandSender> sender, final @NotNull String input) {
        return new ArrayList<>(Arrays.asList("walk", "fly"));
    }

    private enum SpeedType {
        FLY("fly", "f"),
        WALK("walk", "w", "run", "r");
        final String[] aliases;

        SpeedType(String... aliases) {
            this.aliases = aliases;
        }

        public static SpeedType parseSpeedType(String input) throws IllegalArgumentException {
            for (SpeedType speedType : values()) {
                if (speedType.name().equalsIgnoreCase(input)) {
                    return speedType;
                }
                for (String alias : speedType.aliases) {
                    if (alias.equalsIgnoreCase(input)) {
                        return speedType;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid input: " + input);
        }

        public String getName() {
            return aliases[0];
        }
    }
}

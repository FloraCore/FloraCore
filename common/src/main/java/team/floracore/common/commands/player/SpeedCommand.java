package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.*;
import cloud.commandframework.context.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Speed命令
 */
@CommandPermission("floracore.command.speed")
public class SpeedCommand extends AbstractFloraCoreCommand {
    public SpeedCommand(FloraCorePlugin plugin) {
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
    public void speedAdvance(final @NotNull Player p, final @Argument(value = "speed", suggestions = "speeds") float speed, final @Argument(value = "type", suggestions = "types") String type, final @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
        Sender sender = getPlugin().getSenderFactory().wrap(p);
        try {
            final SpeedType speedType = SpeedType.parseSpeedType(type);
            final boolean isBypass = p.hasPermission("floracore.command.speed.bypass");
            if (target != null) {
                if (!p.hasPermission("floracore.command.speed.other")) {
                    Message.NO_PERMISSION_FOR_SUBCOMMANDS.send(sender);
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
                    Message.COMMAND_SPEED_OTHER.send(sender, p.getDisplayName(), speedType == SpeedType.FLY ? Message.COMMAND_MISC_SPEED_FLY.build() : Message.COMMAND_MISC_SPEED_WALK.build(), String.valueOf(speed));
                }
                Message.COMMAND_SPEED.send(sender, target.getDisplayName(), speedType == SpeedType.FLY ? Message.COMMAND_MISC_SPEED_FLY.build() : Message.COMMAND_MISC_SPEED_WALK.build(), String.valueOf(speed));
            } else {
                switch (speedType) {
                    case FLY:
                        p.setFlySpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                    case WALK:
                        p.setWalkSpeed(getRealMoveSpeed(speed, speedType, isBypass));
                        break;
                }
                Message.COMMAND_SPEED.send(sender, p.getDisplayName(), speedType == SpeedType.FLY ? Message.COMMAND_MISC_SPEED_FLY.build() : Message.COMMAND_MISC_SPEED_WALK.build(), String.valueOf(speed));
            }
        } catch (IllegalArgumentException e) {
            Message.COMMAND_SPEED_NO_SUCH.send(sender, type);
        }
    }

    private float getRealMoveSpeed(final float userSpeed, final SpeedType speedType, final boolean isBypass) {
        final float defaultSpeed = speedType == SpeedType.FLY ? 0.1f : 0.2f;
        float maxSpeed = 1f;
        if (!isBypass) {
            maxSpeed = (speedType == SpeedType.FLY ? getPlugin().getConfiguration().get(ConfigKeys.SPEED_MAX_FLY_SPEED) : getPlugin().getConfiguration().get(ConfigKeys.SPEED_MAX_WALK_SPEED)).floatValue();
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

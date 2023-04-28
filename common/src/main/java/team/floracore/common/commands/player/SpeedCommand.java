package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.config.*;
import team.floracore.common.locale.*;
import team.floracore.common.plugin.*;
import team.floracore.common.sender.*;

@CommandPermission("floracore.command.speed")
public class SpeedCommand extends AbstractFloraCoreCommand {
    public SpeedCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("speed <speed> [type] [target]")
    @CommandDescription("传送至你当前位置的最高点设置你（或指定玩家）的指定类型的速度")
    public void speed(final @NotNull Player p, final @Argument("speed") float speed, final @Argument("type") String type, final @Argument("target") Player target, final @Nullable @Flag("silent") Boolean silent) {
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
                    // TODO 向发送修改成功
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
            Message.COMMAND_SPEED_NOSUCH.send(sender, type);
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
    }
}

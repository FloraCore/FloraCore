package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.entity.*;
import org.jetbrains.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

@CommandPermission("floracore.command.speed")
public class SpeedCommand extends AbstractFloraCoreCommand {
    public SpeedCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("speed <data> [type] [target]")
    @CommandDescription("传送至你当前位置的最高点设置你（或指定玩家）的指定类型的速度")
    public void speed(final @NotNull Player p, final @Argument("data") float data, final @Argument("type") String type, final @Argument("target") Player target) {
        try {
            final SpeedType speedType = SpeedType.parseSpeedType(type);
            final boolean isBypass = p.hasPermission("floracore.command.speed.bypass");
        } catch (IllegalArgumentException e) {
            // TODO 无法识别的类型
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

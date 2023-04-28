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

    // TODO 未完成的命令
    // /speed <speed> (type) (player)
    @CommandMethod("speed <data> [type] [target]")
    @CommandDescription("传送至你当前位置的最高点")
    public void speed(final @NotNull Player p, final @Argument("data") float data, final @Argument("type") String type, final @Argument("target") Player target) {

    }

    private enum SpeedType {
        FLY("fly", "f"),
        WALK("walk", "w");
        final String[] aliases;

        SpeedType(String... aliases) {
            this.aliases = aliases;
        }

        public static SpeedType parseSpeedType(String input) {
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

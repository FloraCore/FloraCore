package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

/**
 * Give命令
 */
@CommandPermission("floracore.command.give")
@CommandDescription("给予玩家指定的物品")
public class GiveCommand extends AbstractFloraCoreCommand {
    public GiveCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    // TODO Give命令
}

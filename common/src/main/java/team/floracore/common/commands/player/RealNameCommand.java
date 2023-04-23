package team.floracore.common.commands.player;

import cloud.commandframework.annotations.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

@CommandPermission("floracore.command.realname")
public class RealNameCommand extends AbstractFloraCoreCommand {
    public RealNameCommand(FloraCorePlugin plugin) {
        super(plugin);
    }
}

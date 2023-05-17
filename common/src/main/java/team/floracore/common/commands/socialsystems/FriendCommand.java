package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.friend")
public class FriendCommand extends AbstractFloraCoreCommand {
    public FriendCommand(FloraCorePlugin plugin) {
        super(plugin);
    }
}

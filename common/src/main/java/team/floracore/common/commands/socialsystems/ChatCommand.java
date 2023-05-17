package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.chat")
public class ChatCommand extends AbstractFloraCoreCommand {
    public ChatCommand(FloraCorePlugin plugin) {
        super(plugin);
    }
}

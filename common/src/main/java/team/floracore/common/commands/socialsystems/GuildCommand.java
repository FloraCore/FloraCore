package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.guild")
public class GuildCommand extends AbstractFloraCoreCommand {
    public GuildCommand(FloraCorePlugin plugin) {
        super(plugin);
    }
}

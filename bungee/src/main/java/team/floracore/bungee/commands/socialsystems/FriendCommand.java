package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import team.floracore.bungee.*;
import team.floracore.bungee.command.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.friend")
public class FriendCommand extends FloraCoreBungeeCommand {
    public FriendCommand(FCBungeePlugin plugin) {
        super(plugin);
    }
}

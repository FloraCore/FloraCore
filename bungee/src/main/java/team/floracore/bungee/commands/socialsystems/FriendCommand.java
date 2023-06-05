package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

@CommandContainer
@CommandPermission("floracore.socialsystems.friend")
public class FriendCommand extends FloraCoreBungeeCommand {
    public FriendCommand(FCBungeePlugin plugin) {
        super(plugin);
    }
}

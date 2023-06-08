package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

@CommandContainer
@CommandDescription("floracore.command.description.friend")
@CommandPermission("floracore.socialsystems.friend")
public class FriendCommand extends FloraCoreBungeeCommand {
    public FriendCommand(FCBungeePlugin plugin) {
        super(plugin);
    }
}

package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

@CommandContainer
@CommandPermission("floracore.socialsystems.guild")
public class GuildCommand extends FloraCoreBungeeCommand {
    public GuildCommand(FCBungeePlugin plugin) {
        super(plugin);
    }
}

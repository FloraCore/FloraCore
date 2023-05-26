package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import team.floracore.bungee.*;
import team.floracore.bungee.command.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.guild")
public class GuildCommand extends FloraCoreBungeeCommand {
    public GuildCommand(FCBungeePlugin plugin) {
        super(plugin);
    }
}

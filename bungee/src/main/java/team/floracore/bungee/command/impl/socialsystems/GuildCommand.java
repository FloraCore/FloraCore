package team.floracore.bungee.command.impl.socialsystems;

import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

@CommandContainer
@CommandDescription("floracore.command.description.guild")
@CommandPermission("floracore.socialsystems.guild")
public class GuildCommand extends FloraCoreBungeeCommand {
	public GuildCommand(FCBungeePlugin plugin) {
		super(plugin);
	}
}

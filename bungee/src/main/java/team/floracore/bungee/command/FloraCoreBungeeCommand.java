package team.floracore.bungee.command;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.common.command.AbstractFloraCoreCommand;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FloraCoreBungeeCommand extends AbstractFloraCoreCommand {
	private final FCBungeePlugin plugin;

	public FloraCoreBungeeCommand(FCBungeePlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	public FCBungeePlugin getPlugin() {
		return plugin;
	}

	@Suggestions("onlinePlayers")
	public @NotNull List<String> getOnlinePlayers(final @NotNull CommandContext<CommandSender> sender,
	                                              final @NotNull String input) {
		CommandSender s = sender.getSender();
		ProxiedPlayer p = (ProxiedPlayer) s;
		return p.getServer().getInfo().getPlayers().stream()
				.sorted(Comparator.comparing(ProxiedPlayer::getName))
				.map(ProxiedPlayer::getName)
				.collect(Collectors.toList());
	}
}

package team.floracore.bukkit.command;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.common.command.AbstractFloraCoreCommand;
import team.floracore.common.sender.Sender;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FloraCoreBukkitCommand extends AbstractFloraCoreCommand {
	private final FCBukkitPlugin plugin;

	public FloraCoreBukkitCommand(FCBukkitPlugin plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	@Suggestions("onlinePlayers")
	public @NotNull List<String> getOnlinePlayers(final @NotNull CommandContext<CommandSender> sender,
	                                              final @NotNull String input) {
		return plugin.getOnlineSenders()
				.sorted(Comparator.comparing(Sender::getDisplayName))
				.map(Sender::getDisplayName)
				.collect(Collectors.toList());
	}

	@Suggestions("worlds-all")
	public @NotNull List<String> getWorldsWithAll(final @NotNull CommandContext<CommandSender> sender,
	                                              final @NotNull String input) {
		final List<String> worlds = getWorlds(sender, input);
		worlds.add("*");
		return worlds;
	}

	@Suggestions("worlds")
	public @NotNull List<String> getWorlds(final @NotNull CommandContext<CommandSender> sender,
	                                       final @NotNull String input) {
		final List<String> worlds = Lists.newArrayList();
		for (final World world : plugin.getBootstrap().getServer().getWorlds()) {
			worlds.add(world.getName());
		}
		return worlds;
	}

	@Suggestions("commonDurations")
	public @NotNull List<String> getCommonDurations(final @NotNull CommandContext<CommandSender> sender,
	                                                final @NotNull String input) {
		return ImmutableList.of("1", "60", "600", "3600", "86400");
	}

	public ProtocolManager getProtocolManager() {
		return plugin.getProtocolManager();
	}
}

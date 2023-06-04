package team.floracore.bukkit.commands.player;

import cloud.commandframework.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;

/**
 * Feed命令
 */
@CommandPermission("floracore.command.feed")
@CommandDescription("floracore.command.description.feed")
public class FeedCommand extends FloraCoreBukkitCommand {
	public FeedCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("feed")
	@CommandDescription("floracore.command.description.feed.self")
	public void self(@NotNull Player s) {
		feed(s);
		PlayerCommandMessage.COMMAND_FEED_SELF.send(getPlugin().getSenderFactory().wrap(s));
	}

	private void feed(@NotNull Player player) {
		player.setFoodLevel(20);
		player.setSaturation(20.0F);
	}

	@CommandMethod("feed <target>")
	@CommandPermission("floracore.command.feed.other")
	@CommandDescription("floracore.command.description.feed.other")
	public void other(@NotNull CommandSender s,
	                  @NotNull @Argument("target") Player target,
	                  @Nullable @Flag("silent") Boolean silent) {
		feed(target);
		PlayerCommandMessage.COMMAND_FEED_OTHER.send(getPlugin().getSenderFactory().wrap(s), target.getName());
		if (silent == null || !silent) {
			PlayerCommandMessage.COMMAND_FEED_FROM.send(getPlugin().getSenderFactory().wrap(target), s.getName());
		}
	}
}

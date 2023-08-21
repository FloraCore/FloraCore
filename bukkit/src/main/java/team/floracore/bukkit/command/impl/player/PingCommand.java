package team.floracore.bukkit.command.impl.player;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import team.floracore.bukkit.FCBukkitPlugin;
import team.floracore.bukkit.command.FloraCoreBukkitCommand;
import team.floracore.bukkit.locale.message.commands.PlayerCommandMessage;
import team.floracore.bukkit.util.wrappednms.NmsEntityPlayer;
import team.floracore.bukkit.util.wrappedobc.ObcEntity;
import team.floracore.bukkit.util.wrappedobc.ObcPlayer;
import team.floracore.bukkit.util.wrapper.BukkitWrapper;
import team.floracore.common.util.wrapper.WrappedObject;

/**
 * Ping命令
 */
@CommandDescription("floracore.command.description.ping")
@CommandPermission("floracore.command.ping")
public class PingCommand extends FloraCoreBukkitCommand {
	public PingCommand(FCBukkitPlugin plugin) {
		super(plugin);
	}

	@CommandMethod("ping")
	@CommandDescription("floracore.command.description.ping.self")
	public void self(@NotNull Player s) {
		PlayerCommandMessage.COMMAND_PING_SELF.send(getPlugin().getSenderFactory().wrap(s), getPing(s));
	}

	public int getPing(@NotNull Player player) {
		if (BukkitWrapper.v17) {
			ObcPlayer op = WrappedObject.wrap(ObcPlayer.class, player);
			assert op != null;
			return op.getPing();
		}
		ObcEntity obcEntity = WrappedObject.wrap(ObcEntity.class, player);
		assert obcEntity != null;
		NmsEntityPlayer nep = obcEntity.getHandle().cast(NmsEntityPlayer.class);
		return nep.getPing();
	}

	@CommandMethod("ping <target>")
	@CommandDescription("floracore.command.description.ping")
	@CommandPermission("floracore.command.ping.other")
	public void other(@NotNull CommandSender s, @NotNull @Argument("target") Player target) {
		PlayerCommandMessage.COMMAND_PING_OTHER.send(getPlugin().getSenderFactory().wrap(s),
				target.getName(),
				getPing(target));
	}
}
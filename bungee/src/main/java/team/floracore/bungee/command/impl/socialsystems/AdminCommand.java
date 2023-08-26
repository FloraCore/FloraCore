package team.floracore.bungee.command.impl.socialsystems;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.floracore.api.data.chat.ChatType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;
import team.floracore.bungee.locale.message.SocialSystemsMessage;
import team.floracore.common.sender.Sender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@CommandContainer
@CommandDescription("floracore.command.description.admin")
@CommandPermission("floracore.socialsystems.admin")
public class AdminCommand extends FloraCoreBungeeCommand {
	public AdminCommand(FCBungeePlugin plugin) {
		super(plugin);
	}

	@CommandMethod("adminchat|ac <message>")
	@CommandDescription("floracore.command.description.admin.chat")
	public void adminChat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
		chat(player, message);
	}

	@CommandMethod("admin|a chat <message>")
	@CommandDescription("floracore.command.description.admin.chat")
	public void chat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
		UUID uuid = player.getUniqueId();
		getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
				.pushChatMessage(UUID.randomUUID(),
						ChatType.ADMIN,
						Arrays.asList(uuid.toString(), message)));
		long time = System.currentTimeMillis();
		getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.ADMIN,
				"",
				uuid,
				message,
				time));
	}

	@CommandMethod("admin|a list")
	public void list(final @NotNull CommandSender commandSender) {
		Sender sender = getPlugin().getSenderFactory().wrap(commandSender);
		getAsyncExecutor().execute(() -> {
			List<UUID> members = new ArrayList<>();
			getPlugin().getOnlineSenders().forEach(m -> {
				if (m.hasPermission("floracore.socialsystems.admin") && !m.isConsole()) {
					members.add(m.getUniqueId());
				}
			});
			SocialSystemsMessage.COMMAND_MISC_ADMIN_LIST.send(sender, members);
		});
	}
}

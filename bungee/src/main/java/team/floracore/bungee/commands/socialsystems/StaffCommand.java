package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.floracore.api.bungee.messenger.message.type.ChatMessage;
import org.floracore.api.data.chat.ChatType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

import java.util.Arrays;
import java.util.UUID;

@CommandContainer
@CommandPermission("floracore.socialsystems.staff")
public class StaffCommand extends FloraCoreBungeeCommand {
    public StaffCommand(FCBungeePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("staffchat|sc <message>")
    public void staffChat(final @NotNull ProxiedPlayer player,
                          final @NotNull @Argument("message") @Greedy String message) {
        chat(player, message);
    }

    @CommandMethod("staff|s chat <message>")
    @CommandDescription("让你在STAFF频道中发言")
    public void chat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
        UUID uuid = player.getUniqueId();
        getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                .pushChatMessage(UUID.randomUUID(),
                        ChatMessage.ChatMessageType.STAFF,
                        Arrays.asList(uuid.toString(), message)));
        long time = System.currentTimeMillis();
        getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.STAFF, "", uuid, message, time));
    }
}

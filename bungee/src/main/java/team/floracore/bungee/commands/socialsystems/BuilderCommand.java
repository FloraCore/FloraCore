package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.floracore.api.data.chat.ChatType;
import org.jetbrains.annotations.NotNull;
import team.floracore.bungee.FCBungeePlugin;
import team.floracore.bungee.command.FloraCoreBungeeCommand;

import java.util.Arrays;
import java.util.UUID;

@CommandContainer
@CommandDescription("floracore.command.description.builder")
@CommandPermission("floracore.socialsystems.builder")
public class BuilderCommand extends FloraCoreBungeeCommand {
    public BuilderCommand(FCBungeePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("builder chat <message>")
    @CommandDescription("floracore.command.description.builder.chat")
    public void chat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
        UUID uuid = player.getUniqueId();
        getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                .pushChatMessage(UUID.randomUUID(),
                        ChatType.BUILDER,
                        Arrays.asList(uuid.toString(), message)));
        long time = System.currentTimeMillis();
        getAsyncExecutor().execute(() -> getStorageImplementation().insertChat(ChatType.BUILDER,
                "",
                uuid,
                message,
                time));
    }
}

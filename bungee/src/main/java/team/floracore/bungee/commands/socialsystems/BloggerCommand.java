package team.floracore.bungee.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import net.md_5.bungee.api.connection.*;
import org.floracore.api.bungee.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.bungee.*;
import team.floracore.bungee.command.*;

import java.util.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.blogger")
public class BloggerCommand extends FloraCoreBungeeCommand {
    public BloggerCommand(FCBungeePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("blogger chat <message>")
    @CommandDescription("让你在博主频道中发言")
    public void chat(final @NotNull ProxiedPlayer player, final @NotNull @Argument("message") @Greedy String message) {
        UUID uuid = player.getUniqueId();
        getAsyncExecutor().execute(() -> getPlugin().getBungeeMessagingFactory()
                .pushChatMessage(UUID.randomUUID(),
                        ChatMessage.ChatMessageType.BLOGGER,
                        Arrays.asList(uuid.toString(), message)));
    }
}

package team.floracore.common.commands.socialsystems;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.annotations.specifier.*;
import org.bukkit.entity.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.command.*;
import team.floracore.common.plugin.*;

import java.util.*;

@CommandContainer
@CommandPermission("floracore.socialsystems.blogger")
public class BloggerCommand extends AbstractFloraCoreCommand {
    public BloggerCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("blogger chat <message>")
    public void chat(final @NonNull Player player, final @NonNull @Argument("message") @Greedy String message) {
        UUID uuid = player.getUniqueId();
        getAsyncExecutor().execute(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.BLOGGER, Arrays.asList(uuid.toString(), message));
            });
        });
    }
}

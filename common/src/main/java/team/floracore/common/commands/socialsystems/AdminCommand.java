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
@CommandPermission("floracore.socialsystems.admin")
public class AdminCommand extends AbstractFloraCoreCommand {
    public AdminCommand(FloraCorePlugin plugin) {
        super(plugin);
    }

    @CommandMethod("admin chat <message>")
    public void chat(final @NonNull Player player, final @NonNull @Argument("message") @Greedy String message) {
        UUID uuid = player.getUniqueId();
        getAsyncExecutor().execute(() -> {
            getPlugin().getMessagingService().ifPresent(service -> {
                service.pushChatMessage(UUID.randomUUID(), ChatMessage.ChatMessageType.ADMIN, new String[]{uuid.toString(), message});
            });
        });
    }
}

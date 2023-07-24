package team.floracore.common.messaging.message;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.messenger.message.type.BungeeCommandMessage;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.util.gson.JObject;

import java.util.Optional;
import java.util.UUID;

/**
 * @author xLikeWATCHDOG
 */
public class BungeeCommandImpl extends AbstractMessage implements BungeeCommandMessage {
    public static final String TYPE = "common:bungee-command";
    private final String command;

    public BungeeCommandImpl(UUID id, String command) {
        super(id);
        this.command = command;
    }


    public static BungeeCommandImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        String command = Optional.ofNullable(content.getAsJsonObject().get("command"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no command argument: " + content));
        return new BungeeCommandImpl(id, command);
    }

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("command", this.command).toJson()
        );
    }

    @Override
    public @NotNull String getCommand() {
        return this.command;
    }
}

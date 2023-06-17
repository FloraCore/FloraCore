package team.floracore.common.messaging.message;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.messenger.message.type.KickMessage;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.util.gson.JObject;

import java.util.Optional;
import java.util.UUID;

public class KickMessageImpl extends AbstractMessage implements KickMessage {
    public static final String TYPE = "bukkit:kick";

    private final UUID receiver;
    private final String reason;

    public KickMessageImpl(UUID id, UUID receiver, String reason) {
        super(id);
        this.receiver = receiver;
        this.reason = reason;
    }

    public static KickMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        UUID receiver = Optional.ofNullable(content.getAsJsonObject().get("receiver"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no receiver argument: " + content));
        String reason = Optional.ofNullable(content.getAsJsonObject().get("reason"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no receiver argument: " + content));
        return new KickMessageImpl(id, receiver, reason);
    }

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("receiver", this.receiver.toString()).toJson()
        );
    }

    @Override
    public @NotNull UUID getReceiver() {
        return receiver;
    }

    @Override
    public @NotNull String getReason() {
        return reason;
    }
}

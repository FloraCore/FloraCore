package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.common.messaging.*;
import team.floracore.common.util.gson.*;

import java.util.*;

public class TeleportMessageImpl extends AbstractMessage implements TeleportMessage {
    public static final String TYPE = "teleport";
    private final UUID sender;
    private final UUID recipient;
    private final String serverName;

    public TeleportMessageImpl(UUID id, UUID sender, UUID recipient, String serverName) {
        super(id);
        this.sender = sender;
        this.recipient = recipient;
        this.serverName = serverName;
    }

    public static TeleportMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }

        UUID sender = Optional.ofNullable(content.getAsJsonObject().get("sender"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no sender argument: " + content));

        UUID recipient = Optional.ofNullable(content.getAsJsonObject().get("recipient"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no recipient argument: " + content));

        String serverName = Optional.ofNullable(content.getAsJsonObject().get("serverName"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no serverName argument: " + content));

        return new TeleportMessageImpl(id, sender, recipient, serverName);
    }

    @Override
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("sender", this.sender.toString())
                        .add("recipient", this.recipient.toString())
                        .add("serverName", serverName).toJson()
        );
    }


    @Override
    public @NotNull String getServerName() {
        return serverName;
    }

    @Override
    public @NotNull UUID getSender() {
        return sender;
    }

    @Override
    public @NotNull UUID getRecipient() {
        return recipient;
    }
}

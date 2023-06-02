package team.floracore.bukkit.messaging.message;

import com.google.gson.JsonElement;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.bukkit.messenger.message.type.TeleportMessage;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.messaging.message.AbstractMessage;
import team.floracore.common.util.gson.JObject;

import java.util.Optional;
import java.util.UUID;

public class TeleportMessageImpl extends AbstractMessage implements TeleportMessage {
    public static final String TYPE = "bukkit:teleport";
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
                .orElseThrow(() -> new IllegalStateException(
                        "Incoming message has no recipient argument: " + content));

        String serverName = Optional.ofNullable(content.getAsJsonObject().get("serverName"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException(
                        "Incoming message has no serverName argument: " + content));

        return new TeleportMessageImpl(id, sender, recipient, serverName);
    }

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("sender", this.sender.toString())
                        .add("recipient", this.recipient.toString())
                        .add("serverName", serverName).toJson()
        );
    }

    @Override
    public @NotNull UUID getSender() {
        return sender;
    }

    @Override
    public @NotNull UUID getRecipient() {
        return recipient;
    }

    @Override
    public @NotNull String getServerName() {
        return serverName;
    }
}

package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.common.messaging.*;
import team.floracore.common.util.gson.*;

import java.util.*;

public class ConnectServerMessageImpl extends AbstractMessage implements ConnectServerMessage {
    public static final String TYPE = "connect-server";
    private final UUID recipient;
    private final String serverName;

    public ConnectServerMessageImpl(UUID id, UUID recipient, String serverName) {
        super(id);
        this.recipient = recipient;
        this.serverName = serverName;
    }

    public static ConnectServerMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        UUID recipient = Optional.ofNullable(content.getAsJsonObject().get("recipient"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no recipient argument: " + content));

        String serverName = Optional.ofNullable(content.getAsJsonObject().get("serverName"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no serverName argument: " + content));

        return new ConnectServerMessageImpl(id, recipient, serverName);
    }

    @Override
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("recipient", this.recipient.toString())
                        .add("serverName", serverName).toJson()
        );
    }


    @Override
    public @NotNull String getServerName() {
        return serverName;
    }

    @Override
    public @NotNull UUID getRecipient() {
        return recipient;
    }
}

package team.floracore.common.messaging;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.messenger.IncomingMessageConsumer;
import org.floracore.api.messenger.Messenger;
import org.floracore.api.messenger.MessengerProvider;
import org.jetbrains.annotations.NotNull;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.util.ExpiringSet;
import team.floracore.common.util.gson.GsonProvider;
import team.floracore.common.util.gson.JObject;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FloraCoreMessagingService implements InternalMessagingService, IncomingMessageConsumer {
    private final FloraCorePlugin plugin;
    private final ExpiringSet<UUID> receivedMessages;

    private final MessengerProvider messengerProvider;
    private final Messenger messenger;

    public FloraCoreMessagingService(FloraCorePlugin plugin, MessengerProvider messengerProvider) {
        this.plugin = plugin;

        this.messengerProvider = messengerProvider;
        this.messenger = messengerProvider.obtain(this);
        Objects.requireNonNull(this.messenger, "messenger");

        this.receivedMessages = new ExpiringSet<>(1, TimeUnit.HOURS);
    }

    public static String encodeMessageAsString(String type, UUID id, @Nullable JsonElement content) {
        JsonObject json = new JObject()
                .add("id", id.toString())
                .add("type", type)
                .consume(o -> {
                    if (content != null) {
                        o.add("content", content);
                    }
                })
                .toJson();

        return GsonProvider.normal().toJson(json);
    }

    @Override
    public String getName() {
        return this.messengerProvider.getName();
    }

    @Override
    public Messenger getMessenger() {
        return this.messenger;
    }

    @Override
    public MessengerProvider getMessengerProvider() {
        return this.messengerProvider;
    }

    @Override
    public void close() {
        this.messenger.close();
    }

    @Override
    public UUID generatePingId() {
        UUID uuid = UUID.randomUUID();
        this.receivedMessages.add(uuid);
        return uuid;
    }

    @Override
    public ExpiringSet<UUID> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void addReceivedMessage(UUID uuid) {
        this.receivedMessages.add(uuid);
    }

    @Override
    public boolean consumeIncomingMessageAsString(@NotNull String encodedString) {
        try {
            return consumeIncomingMessageAsString0(encodedString);
        } catch (Exception e) {
            this.plugin.getLogger()
                    .warn("Unable to decode incoming messaging service message: '" + encodedString + "'", e);
            return false;
        }
    }

    private boolean consumeIncomingMessageAsString0(@NotNull String encodedString) {
        Objects.requireNonNull(encodedString, "encodedString");
        JsonObject parsed = Objects.requireNonNull(GsonProvider.normal().fromJson(encodedString, JsonObject.class),
                "parsed");
        JsonObject json = parsed.getAsJsonObject();

        // extract id
        JsonElement idElement = json.get("id");
        if (idElement == null) {
            throw new IllegalStateException("Incoming message has no id argument: " + encodedString);
        }
        UUID id = UUID.fromString(idElement.getAsString());

        // ensure the message hasn't been received already
        if (!this.receivedMessages.add(id)) {
            return false;
        }

        // extract type
        JsonElement typeElement = json.get("type");
        if (typeElement == null) {
            throw new IllegalStateException("Incoming message has no type argument: " + encodedString);
        }
        String type = typeElement.getAsString();

        // extract content
        @Nullable JsonElement content = json.get("content");

        return plugin.processIncomingMessage(type, content, id);
    }
}

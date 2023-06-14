package team.floracore.bungee.messaging.message;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import team.floracore.api.bungee.messenger.message.type.ChatMessage;
import team.floracore.api.data.chat.ChatType;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.messaging.message.AbstractMessage;
import team.floracore.common.util.gson.GsonProvider;
import team.floracore.common.util.gson.JObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Bungee 聊天频道
 *
 * @author xLikeWATCHDOG
 */
public class ChatMessageImpl extends AbstractMessage implements ChatMessage {
    public static final String TYPE = "bungee:chat";

    private final UUID receiver;
    private final ChatType type;
    private final List<String> parameters;

    public ChatMessageImpl(UUID id, UUID receiver, ChatType type, List<String> parameters) {
        super(id);
        this.receiver = receiver;
        this.type = type;
        this.parameters = parameters;
    }

    public static ChatMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        UUID receiver = Optional.ofNullable(content.getAsJsonObject().get("receiver"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no receiver argument: " + content));

        ChatType type = Optional.ofNullable(content.getAsJsonObject().get("type"))
                .map(JsonElement::getAsString)
                .map(ChatType::valueOf)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no type argument: " + content));

        Type type1 = new TypeToken<List<String>>() {
        }.getType();
        String p = Optional.ofNullable(content.getAsJsonObject().get("parameters"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no parameters argument: " + content));
        List<String> parameters = GsonProvider.normal().fromJson(p, type1);

        return new ChatMessageImpl(id, receiver, type, parameters);
    }

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("receiver", this.receiver.toString())
                        .add("type", this.type.toString())
                        .add("parameters", GsonProvider.normal().toJson(this.parameters))
                        .toJson()
        );
    }

    @Override
    public @NotNull UUID getReceiver() {
        return this.receiver;
    }

    @Override
    public @NotNull ChatType getType() {
        return this.type;
    }

    @Override
    public @NotNull List<String> getParameters() {
        return this.parameters;
    }
}

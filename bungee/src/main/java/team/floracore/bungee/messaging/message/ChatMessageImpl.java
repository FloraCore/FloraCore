package team.floracore.bungee.messaging.message;

import com.google.gson.*;
import com.google.gson.reflect.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.bungee.messenger.message.type.*;
import team.floracore.common.messaging.*;
import team.floracore.common.messaging.message.*;
import team.floracore.common.util.gson.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Bungee 聊天频道
 *
 * @author xLikeWATCHDOG
 * @date 2023/5/26 17:15
 */
public class ChatMessageImpl extends AbstractMessage implements ChatMessage {
    public static final String TYPE = "bungee:chat";

    private final UUID receiver;
    private final ChatMessageType type;
    private final List<String> parameters;

    public ChatMessageImpl(UUID id, UUID receiver, ChatMessageType type, List<String> parameters) {
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

        ChatMessageType type = Optional.ofNullable(content.getAsJsonObject().get("type"))
                .map(JsonElement::getAsString)
                .map(ChatMessageType::valueOf)
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
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("receiver", this.receiver.toString())
                        .add("type", this.type.toString())
                        .add("parameters", GsonProvider.normal().toJson(this.parameters)).toJson()
        );
    }

    @Override
    public @NonNull UUID getReceiver() {
        return this.receiver;
    }

    @Override
    public @NonNull ChatMessageType getType() {
        return this.type;
    }

    @Override
    public @NonNull List<String> getParameters() {
        return this.parameters;
    }
}

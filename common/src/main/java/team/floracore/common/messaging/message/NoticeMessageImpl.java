package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.messaging.*;
import team.floracore.common.util.gson.*;

import java.util.*;

public class NoticeMessageImpl extends AbstractMessage implements NoticeMessage {
    public static final String TYPE = "notice";

    private final UUID receiver;
    private final NoticeType type;
    private final String[] parameters;

    public NoticeMessageImpl(UUID id, UUID receiver, NoticeType type, String[] parameters) {
        super(id);
        this.receiver = receiver;
        this.type = type;
        this.parameters = parameters;
    }

    public static NoticeMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        UUID receiver = Optional.ofNullable(content.getAsJsonObject().get("receiver"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no receiver argument: " + content));

        NoticeType type = Optional.ofNullable(content.getAsJsonObject().get("type"))
                .map(JsonElement::getAsString)
                .map(NoticeType::valueOf)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no type argument: " + content));

        String[] parameters = Optional.ofNullable(content.getAsJsonObject().get("parameters"))
                .map(JsonElement::getAsString)
                .map(str -> str.split(","))
                .orElseThrow(() -> new IllegalStateException("Incoming message has no parameters argument: " + content));

        return new NoticeMessageImpl(id, receiver, type, parameters);
    }

    @Override
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("receiver", this.receiver.toString())
                        .add("type", this.type.toString())
                        .add("parameters", Arrays.toString(this.parameters)).toJson()
        );
    }

    @Override
    public @NonNull UUID getReceiver() {
        return this.receiver;
    }

    @Override
    public @NonNull NoticeType getType() {
        return this.type;
    }

    @Override
    public @NonNull String[] getParameters() {
        return this.parameters;
    }
}

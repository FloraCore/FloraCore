package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.floracore.api.messenger.message.type.*;
import org.jetbrains.annotations.*;
import team.floracore.common.messaging.*;
import team.floracore.common.util.gson.*;

import java.util.*;

public class ChangeNameMessageImpl extends AbstractMessage implements ChangeNameMessage {
    public static final String TYPE = "change-name";
    private final UUID changer;
    private final String name;

    public ChangeNameMessageImpl(UUID id, UUID changer,String name) {
        super(id);
        this.changer = changer;
        this.name = name;
    }

    public static ChangeNameMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }

        UUID changer = Optional.ofNullable(content.getAsJsonObject().get("changer"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no changer argument: " + content));

        String name = Optional.ofNullable(content.getAsJsonObject().get("name"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no name argument: " + content));

        return new ChangeNameMessageImpl(id, changer,name);
    }

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("changer", this.changer.toString())
                        .add("name", name).toJson()
        );
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public UUID getChanger() {
        return changer;
    }
}

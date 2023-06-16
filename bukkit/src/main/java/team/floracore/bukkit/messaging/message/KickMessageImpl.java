package team.floracore.bukkit.messaging.message;

import org.jetbrains.annotations.NotNull;
import team.floracore.api.bukkit.messenger.message.type.KickMessage;
import team.floracore.common.messaging.FloraCoreMessagingService;
import team.floracore.common.messaging.message.AbstractMessage;
import team.floracore.common.util.gson.JObject;

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

    @Override
    public @NotNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("receiver", receiver.toString())
                        .add("reason", reason).toJson()
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

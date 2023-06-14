package team.floracore.common.messaging.message;

import org.jetbrains.annotations.NotNull;
import team.floracore.api.messenger.message.Message;
import team.floracore.api.messenger.message.OutgoingMessage;

import java.util.UUID;

public abstract class AbstractMessage implements Message, OutgoingMessage {
    private final UUID id;

    public AbstractMessage(UUID id) {
        this.id = id;
    }

    @Override
    public @NotNull UUID getId() {
        return this.id;
    }
}

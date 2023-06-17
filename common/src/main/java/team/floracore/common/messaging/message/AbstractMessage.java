package team.floracore.common.messaging.message;

import org.floracore.api.messenger.message.Message;
import org.floracore.api.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;

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

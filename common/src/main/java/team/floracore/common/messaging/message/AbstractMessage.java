package team.floracore.common.messaging.message;

import org.floracore.api.messenger.message.*;
import org.jetbrains.annotations.*;

import java.util.*;

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

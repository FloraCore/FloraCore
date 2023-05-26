package team.floracore.common.messaging.message;

import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.*;

import java.util.*;

public abstract class AbstractMessage implements Message, OutgoingMessage {
    private final UUID id;

    public AbstractMessage(UUID id) {
        this.id = id;
    }

    @Override
    public @NonNull UUID getId() {
        return this.id;
    }
}
